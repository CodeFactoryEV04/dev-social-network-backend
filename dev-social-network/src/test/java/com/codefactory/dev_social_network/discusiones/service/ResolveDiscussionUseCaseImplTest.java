package com.codefactory.dev_social_network.discusiones.service;

import com.codefactory.dev_social_network.discusiones.dto.ResolveDiscussionResponse;
import com.codefactory.dev_social_network.discusiones.entity.Discussion;
import com.codefactory.dev_social_network.discusiones.entity.DiscussionStatus;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionFinderPort;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolutionRule;
import com.codefactory.dev_social_network.discusiones.interfaces.DiscussionResolverPort;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


public class ResolveDiscussionUseCaseImplTest {

  @InjectMocks
  private ResolveDiscussionUseCaseImpl useCase;

  @Mock
  private DiscussionFinderPort discussionFinderPort;

  @Mock
  private DiscussionResolverPort discussionResolverPort;

  @Spy
  private List<DiscussionResolutionRule> rules = List.of(
      new OnlyAuthorCanResolveRule(),
      new DiscussionNotAlreadyResolvedRule()
  );

  private UUID userId;

  private Discussion discussion;

  private ResolveDiscussionResponse response;


  void buildTestObjects() {
    this.userId = UUID.randomUUID();
    this.discussion = new Discussion(userId,
        3L, "Test Title", "Test Content");
  }

  void buildSuccessResponse() {
    this.response = new ResolveDiscussionResponse(
        discussion.getId(),
        DiscussionStatus.RESOLVED,
        "La discusión ha sido marcada como resuelta"
    );
  }

  @Test
  void MarkDiscussionAsResolvedSuccessFully() {
    buildTestObjects();
    buildSuccessResponse();
    var actual = useCase.resolve(userId, discussion.getId());

    this.rules.forEach((rule) ->
        verify(rule).validate(this.discussion, this.userId) // verify all rules was checked
    );

    verify(discussionResolverPort).markAsResolved(this.discussion.getId()); // verify discussion was actually resolved

    assertEquals(actual, this.response);
  }


}
