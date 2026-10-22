package com.codefactory.dev_social_network.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.codefactory.dev_social_network.catalogoTech.service.CatalogoTecnologiaServiceImpl;
import com.codefactory.dev_social_network.shared.exception.AniosExperienciaInvalidosException;
import com.codefactory.dev_social_network.shared.exception.BusinessException;
import com.codefactory.dev_social_network.shared.exception.EnlaceInvalidoException;
import com.codefactory.dev_social_network.shared.exception.HabilidadNoValidaException;
import com.codefactory.dev_social_network.shared.exception.NivelExperienciaInvalidoException;
import com.codefactory.dev_social_network.usuarios.dto.UserProfileEditionRequestDTO;
import com.codefactory.dev_social_network.usuarios.dto.UserProfileResponseDTO;
import com.codefactory.dev_social_network.usuarios.entity.ExperienceLevel;
import com.codefactory.dev_social_network.usuarios.entity.UserExternalLinksEntity;
import com.codefactory.dev_social_network.usuarios.entity.UserHabilitiesEntity;
import com.codefactory.dev_social_network.usuarios.entity.UserProfileEntity;
import com.codefactory.dev_social_network.usuarios.entity.Usuario;
import com.codefactory.dev_social_network.usuarios.interfaces.UserProfileRepositoryPort;
import com.codefactory.dev_social_network.usuarios.interfaces.UsuarioRepositoryPort;
import com.codefactory.dev_social_network.usuarios.repository.UserExternalLinksRepository;
import com.codefactory.dev_social_network.usuarios.repository.UserHabilitiesRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepositoryPort userProfileRepositoryPort;
    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;
    @Mock
    private UserExternalLinksRepository externalLinksRepository;
    @Mock
    private UserHabilitiesRepository habilitiesRepository;
    @Mock
    private CatalogoTecnologiaServiceImpl catalogoTecnologiaService;
    @Mock
    private EnlaceExternoValidator enlaceExternoValidator;

    @InjectMocks
    private UserProfileServiceImpl service;

    private UUID userId;
    private Usuario usuario;
    private UUID profileId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        usuario = new Usuario("Ana", "Ruiz", "ana@correo.com");
        ReflectionTestUtils.setField(usuario, "id", userId);

        when(usuarioRepositoryPort.buscarPorId(userId)).thenReturn(Optional.of(usuario));
        when(externalLinksRepository.findByUserProfile_Id(any())).thenReturn(List.of());
        when(habilitiesRepository.findByUserProfile_Id(any())).thenReturn(List.of());
        // Por defecto toda habilidad existe en el catalogo. Los tests que
        // necesitan el contrario lo sobreescriben explicitamente.
        lenient().when(catalogoTecnologiaService.existe(anyString())).thenReturn(true);
    }

    private UserProfileEntity profileExistente() {
        UserProfileEntity profile = new UserProfileEntity(usuario);
        ReflectionTestUtils.setField(profile, "id", profileId);
        return profile;
    }

    private void perfilEnRepositorio() {
        when(userProfileRepositoryPort.buscarPorUsuarioId(userId))
                .thenReturn(Optional.of(profileExistente()));
    }

    private UserProfileEditionRequestDTO requestValido() {
        UserProfileEditionRequestDTO request = new UserProfileEditionRequestDTO();
        request.setYearsOfExperience(5);
        request.setLevel("SENIOR");
        request.setSkills(List.of("Java"));
        request.setGithubLink("http://github.com/ana-ruiz");
        request.setLinkedinLink("https://www.linkedin.com/in/ana-ruiz/");
        request.setPortfolioLink("https://ana.dev");
        return request;
    }

    // ---------------------------------------------------------------- lectura

    @Test
    @DisplayName("devuelve el perfil con los datos del usuario")
    void devuelvePerfilDelUsuario() {
        // Arrange
        perfilEnRepositorio();

        // Act
        UserProfileResponseDTO respuesta = service.getUserProfile(userId);

        // Assert
        assertThat(respuesta.userId()).isEqualTo(userId);
        assertThat(respuesta.nombre()).isEqualTo("Ana");
        assertThat(respuesta.email()).isEqualTo("ana@correo.com");
    }

    @Test
    @DisplayName("crea un perfil vacio en memoria si el usuario aun no tiene uno")
    void creaPerfilVacioSiNoExiste() {
        // Arrange
        when(userProfileRepositoryPort.buscarPorUsuarioId(userId)).thenReturn(Optional.empty());

        // Act y Assert
        assertThatCode(() -> service.getUserProfile(userId)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("falla al leer el perfil de un usuario inexistente")
    void fallaSiUsuarioNoExiste() {
        // Arrange
        when(usuarioRepositoryPort.buscarPorId(userId)).thenReturn(Optional.empty());

        // Act y Assert
        assertThatThrownBy(() -> service.getUserProfile(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No se encontró un usuario");
    }

    // ------------------------------------------------------------- validaciones

    @Test
    @DisplayName("rechaza mas de 20 anos de experiencia")
    void rechazaAniosFueraDeRango() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setYearsOfExperience(21);

        // Act y Assert
        assertThatThrownBy(() -> service.updateUserProfile(userId, request))
                .isInstanceOf(AniosExperienciaInvalidosException.class);

        // Assert
        verify(userProfileRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("rechaza anos de experiencia negativos")
    void rechazaAnosNegativos() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setYearsOfExperience(-1);

        // Act y Assert
        assertThatThrownBy(() -> service.updateUserProfile(userId, request))
                .isInstanceOf(AniosExperienciaInvalidosException.class);
    }

    @Test
    @DisplayName("acepta unos anos de experiencia nulos")
    void aceptaAnosNulos() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setYearsOfExperience(null);

        // Act y Assert
        assertThatCode(() -> service.updateUserProfile(userId, request)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("rechaza un nivel de experiencia desconocido")
    void rechazaNivelDesconocido() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setLevel("EXPERTO");

        // Act y Assert
        assertThatThrownBy(() -> service.updateUserProfile(userId, request))
                .isInstanceOf(NivelExperienciaInvalidoException.class);
    }

    @Test
    @DisplayName("interpreta el nivel sin importar mayusculas ni espacios")
    void interpretaNivelSinSensibilidad() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setLevel("  junior  ");

        // Act
        UserProfileResponseDTO respuesta = service.updateUserProfile(userId, request);

        // Assert
        assertThat(respuesta.level()).isEqualTo(ExperienceLevel.JUNIOR);
    }

    @Test
    @DisplayName("acepta un nivel vacio y lo deja en null")
    void aceptaNivelVacio() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setLevel("   ");

        // Act y Assert
        assertThatCode(() -> service.updateUserProfile(userId, request)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("rechaza una habilidad que no existe en el catalogo")
    void rechazaHabilidadInexistente() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        when(catalogoTecnologiaService.existe("Java")).thenReturn(false);

        // Act y Assert
        assertThatThrownBy(() -> service.updateUserProfile(userId, request))
                .isInstanceOf(HabilidadNoValidaException.class);

        // Assert
        verify(userProfileRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("rechaza una habilidad nula dentro de la lista")
    void rechazaHabilidadNula() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        // Arrays.asList y no List.of porque List.of no admite null.
        request.setSkills(Arrays.asList("Java", null));

        // Act y Assert
        assertThatThrownBy(() -> service.updateUserProfile(userId, request))
                .isInstanceOf(HabilidadNoValidaException.class);
    }

    @Test
    @DisplayName("acepta una lista de habilidades vacia")
    void aceptaHabilidadesVacias() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setSkills(List.of());

        // Act y Assert
        assertThatCode(() -> service.updateUserProfile(userId, request)).doesNotThrowAnyException();
    }

    // -------------------------------------------------------------- validacion de enlaces

    @Test
    @DisplayName("delega la validacion de los tres enlaces externos")
    void validaLosTresEnlaces() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();

        // Act
        service.updateUserProfile(userId, request);

        // Assert
        verify(enlaceExternoValidator).validarGithub("http://github.com/ana-ruiz");
        verify(enlaceExternoValidator).validarLinkedin("https://www.linkedin.com/in/ana-ruiz/");
        verify(enlaceExternoValidator).validarPortafolio("https://ana.dev");
    }

    @Test
    @DisplayName("si un enlace es invalido no guarda nada")
    void noGuardaSiUnEnlaceEsInvalido() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        doThrow(new EnlaceInvalidoException("malo"))
                .when(enlaceExternoValidator).validarGithub(anyString());

        // Act y Assert
        assertThatThrownBy(() -> service.updateUserProfile(userId, request))
                .isInstanceOf(EnlaceInvalidoException.class);

        // Assert
        verify(userProfileRepositoryPort, never()).guardar(any());
    }

    // ------------------------------------------------------------------ escritura

    @Test
    @DisplayName("actualiza los campos del perfil y lo guarda")
    void actualizaYGuardaElPerfil() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        when(catalogoTecnologiaService.existe("Java")).thenReturn(true);

        // Act
        service.updateUserProfile(userId, request);

        // Assert
        verify(userProfileRepositoryPort).guardar(any(UserProfileEntity.class));
    }

    @Test
    @DisplayName("borra y vuelve a crear los enlaces externos")
    void sincronizaEnlacesExternos() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        when(catalogoTecnologiaService.existe("Java")).thenReturn(true);

        // Act
        service.updateUserProfile(userId, request);

        // Assert
        verify(externalLinksRepository).deleteByUserProfile_Id(profileId);
        verify(externalLinksRepository, times(3)).save(any(UserExternalLinksEntity.class));
    }

    @Test
    @DisplayName("no crea enlaces si los tres vienen vacios")
    void noCreaEnlacesSiVienenVacios() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        request.setGithubLink(null);
        request.setLinkedinLink("   ");
        request.setPortfolioLink(null);

        // Act
        service.updateUserProfile(userId, request);

        // Assert
        verify(externalLinksRepository, never()).save(any(UserExternalLinksEntity.class));
    }

    @Test
    @DisplayName("borra y vuelve a crear las habilidades")
    void sincronizaHabilidades() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        when(catalogoTecnologiaService.existe("Java")).thenReturn(true);

        // Act
        service.updateUserProfile(userId, request);

        // Assert
        verify(habilitiesRepository).deleteByUserProfile_Id(profileId);
        verify(habilitiesRepository, times(1)).save(any(UserHabilitiesEntity.class));
    }

    @Test
    @DisplayName("falla al actualizar el perfil de un usuario inexistente")
    void fallaAlActualizarUsuarioInexistente() {
        // Arrange
        when(usuarioRepositoryPort.buscarPorId(userId)).thenReturn(Optional.empty());

        // Act y Assert
        assertThatThrownBy(() -> service.updateUserProfile(userId, requestValido()))
                .isInstanceOf(BusinessException.class);

        // Assert
        verify(userProfileRepositoryPort, never()).guardar(any());
    }

    // ------------------------------------------------------------------- respuesta

    @Test
    @DisplayName("la respuesta incluye los enlaces y habilidades guardados")
    void respuestaIncluyeEnlacesYHabilidades() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        when(catalogoTecnologiaService.existe("Java")).thenReturn(true);
        UserExternalLinksEntity github = new UserExternalLinksEntity("github", "http://github.com/ana-ruiz", profileExistente());
        UserHabilitiesEntity java = new UserHabilitiesEntity("Java", profileExistente());
        when(externalLinksRepository.findByUserProfile_Id(profileId)).thenReturn(List.of(github));
        when(habilitiesRepository.findByUserProfile_Id(profileId)).thenReturn(List.of(java));

        // Act
        UserProfileResponseDTO respuesta = service.updateUserProfile(userId, request);

        // Assert
        assertThat(respuesta.externalLinks()).hasSize(1);
        assertThat(respuesta.externalLinks().get(0).name()).isEqualTo("github");
        assertThat(respuesta.externalLinks().get(0).url()).isEqualTo("http://github.com/ana-ruiz");
        assertThat(respuesta.skills()).containsExactly("Java");
    }

    @Test
    @DisplayName("la respuesta refleja los anos y el nivel guardados")
    void respuestaReflejaAniosYNivel() {
        // Arrange
        perfilEnRepositorio();
        UserProfileEditionRequestDTO request = requestValido();
        when(catalogoTecnologiaService.existe("Java")).thenReturn(true);

        // Act
        UserProfileResponseDTO respuesta = service.updateUserProfile(userId, request);

        // Assert
        assertThat(respuesta.yearsOfExperience()).isEqualTo(5);
        assertThat(respuesta.level()).isEqualTo(ExperienceLevel.SENIOR);
    }
}

