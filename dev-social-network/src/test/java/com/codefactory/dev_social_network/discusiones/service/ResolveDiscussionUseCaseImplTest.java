package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolutionRule;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolverPort;
import com.codefactory.dev_social_network.shared.exception.DiscusionNoEncontradaException;
import com.codefactory.dev_social_network.shared.exception.DiscusionYaResueltaException;
import com.codefactory.dev_social_network.shared.exception.SoloAutorPuedeResolverException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@Slf4j
public class ResolveDiscussionUseCaseImplTest {

  @Mock
  private DiscussionFinderPort discussionFinderPort;

  @Mock
  private DiscussionResolverPort discussionResolverPort;

  @Spy
  private OnlyAuthorCanResolveRule onlyAuthorCanResolveRule = new OnlyAuthorCanResolveRule();

  @Spy
  private DiscussionNotAlreadyResolvedRule discussionNotAlreadyResolvedRule = new DiscussionNotAlreadyResolvedRule();

  private ResolveDiscussionUseCaseImpl useCase;

  private List<DiscussionResolutionRule> rules;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Build the rules list AFTER openMocks, so it holds the spy instances (the fields
    // only contain spies once openMocks has replaced them), and wire the use case manually.
    this.rules = List.of(
        onlyAuthorCanResolveRule,
        discussionNotAlreadyResolvedRule
    );
    this.useCase = new ResolveDiscussionUseCaseImpl(
        discussionFinderPort,
        discussionResolverPort,
        this.rules
    );
  }

  private UUID userId;

  private Discussion discussion;

  private ResolveDiscussionResponse resolveDiscussionResponse;


  void buildTestObjects() {
    this.userId = UUID.randomUUID();
    this.discussion = new Discussion(userId,
        3L, "Test Title", "Test Content");
  }

  void buildSuccessResponse() {
    this.resolveDiscussionResponse = new ResolveDiscussionResponse(
        discussion.getId(),
        DiscussionStatus.RESOLVED,
        "La discusión ha sido marcada como resuelta"
    );
  }

  @Test
  void MarkDiscussionAsResolvedSuccessfully() {
    buildTestObjects();
    buildSuccessResponse();

    when(discussionFinderPort.findById(this.discussion.getId())).thenReturn(Optional.of(discussion));
    when(discussionResolverPort.markAsResolved(this.discussion.getId())).thenReturn(true);

    var actual = useCase.resolve(userId, discussion.getId());

    this.rules.forEach((rule) ->
        verify(rule, times(1)).validate(this.discussion, this.userId) // verify all rules was checked
    );

    verify(discussionResolverPort).markAsResolved(this.discussion.getId()); // verify discussion was actually resolved

    assertEquals(this.resolveDiscussionResponse, actual);
  }

  @Test
  void markAlreadyResolvedDiscussionAsResolved() {
    buildTestObjects();

    var resolvedDiscussion = this.discussion;
    resolvedDiscussion.setStatus(DiscussionStatus.RESOLVED);

    when(discussionFinderPort.findById(resolvedDiscussion.getId()))
        .thenReturn(Optional.of(resolvedDiscussion));


    assertThrows(DiscusionYaResueltaException.class,
        () -> useCase.resolve(userId, resolvedDiscussion.getId()));

    verify(discussionResolverPort, never()).markAsResolved(any());
  }

  @Test
  void markAsResolvedDiscussionFromOtherUser() {
    buildTestObjects();
    var wrongUserId = UUID.randomUUID();

    when(discussionFinderPort.findById(discussion.getId()))
        .thenReturn(Optional.of(discussion));


    assertThrows(SoloAutorPuedeResolverException.class,
        () -> useCase.resolve(wrongUserId, discussion.getId()));

    verify(discussionResolverPort, never()).markAsResolved(any());
  }

  @Test
  void markAsResolvedNonExistentDiscussion() {
    buildTestObjects();

    when(discussionFinderPort.findById(any()))
        .thenReturn(Optional.empty());

    assertThrows(DiscusionNoEncontradaException.class,
        () -> useCase.resolve(UUID.randomUUID(), 1L));

    verify(discussionResolverPort, never()).markAsResolved(any());
    rules.forEach(rule -> verify(rule, never()).validate(any(), any()));
  }


  @Test
  void discussionCanNotBeResolved() {
    buildTestObjects();

    when(discussionFinderPort.findById(discussion.getId())).thenReturn(Optional.of(discussion));
    rules.forEach(rule -> doNothing().when(rule).validate(discussion, userId));
    when(discussionResolverPort.markAsResolved(discussion.getId())).thenReturn(false);

    assertThrows(DiscusionYaResueltaException.class, () -> useCase.resolve(userId, discussion.getId()));
  }

}
