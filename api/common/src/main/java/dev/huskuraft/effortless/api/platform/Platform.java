package dev.huskuraft.effortless.api.platform;

import dev.huskuraft.effortless.api.core.Item;
import dev.huskuraft.effortless.api.core.ItemStack;
import dev.huskuraft.effortless.api.core.Items;
import dev.huskuraft.effortless.api.core.Resource;
import dev.huskuraft.effortless.api.networking.Buffer;
import dev.huskuraft.effortless.api.tag.TagIoReader;
import dev.huskuraft.effortless.api.tag.TagIoWriter;
import dev.huskuraft.effortless.api.tag.TagRecord;
import dev.huskuraft.effortless.api.text.Text;

import java.util.Locale;
import java.util.Optional;

public interface Platform {

    Resource newResource(String namespace, String path);

    Buffer newBuffer();

    TagRecord newTagRecord();

    Optional<Item> newOptionalItem(Resource resource);

    default Item newItem(Resource resource) {
        return newOptionalItem(resource).orElseThrow();
    }

    ItemStack newItemStack();

    ItemStack newItemStack(Item item, int count);

    ItemStack newItemStack(Item item, int count, TagRecord tag);

    Text newText();

    Text newText(String text);

    Text newText(String text, Text... args);

    Text newTranslatableText(String text);

    Text newTranslatableText(String text, Text... args);

    TagIoReader getTagIoReader();

    TagIoWriter getTagIoWriter();

    default Optional<Item> getOptionalItem(Items items) {
        return newOptionalItem(Resource.of("minecraft", items.name().toLowerCase(Locale.ROOT)));
    }

    default Item getItem(Items items) {
        return getOptionalItem(items).orElseThrow();
    }

}