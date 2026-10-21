package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolutionRule;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolverPort;
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


}
