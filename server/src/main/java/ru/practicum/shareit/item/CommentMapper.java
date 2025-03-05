package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.ItemCommentResponse;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.MergeCommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Mapper
public interface CommentMapper {

    @Mapping(target = "item", source = "item")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", ignore = true)
    Comment createRequestToComment(CreateCommentRequest createCommentRequest, Item item, User author);

    @Mapping(target = "item", source = "item")
    @Mapping(target = "authorName", source = "authorName")
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "created", source = "comment.creationDate")
    MergeCommentResponse commentToMergeResponse(Comment comment, ItemResponse item, String authorName);

    @Mapping(target = "authorName", source = "comment.author.name")
    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "created", source = "comment.creationDate")
    ItemCommentResponse commentToResponse(Comment comment);
}
