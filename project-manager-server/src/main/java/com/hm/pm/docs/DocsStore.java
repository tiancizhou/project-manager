package com.hm.pm.docs;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DocsStore {

    private final AtomicLong spaceIdGenerator = new AtomicLong(100);
    private final AtomicLong pageIdGenerator = new AtomicLong(1000);
    private final AtomicLong revisionIdGenerator = new AtomicLong(3000);

    private final Map<Long, Space> spaces = new ConcurrentHashMap<>();
    private final Map<Long, Page> pages = new ConcurrentHashMap<>();
    private final Map<Long, List<Revision>> revisionsByPage = new ConcurrentHashMap<>();

    public Space createSpace(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("space name is required");
        }
        long id = spaceIdGenerator.incrementAndGet();
        Space space = new Space(id, name);
        spaces.put(id, space);
        return space;
    }

    public Page createPage(Long spaceId, String title, String content) {
        if (spaceId == null || !spaces.containsKey(spaceId)) {
            throw new IllegalArgumentException("spaceId is invalid");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (content == null) {
            throw new IllegalArgumentException("content is required");
        }

        long id = pageIdGenerator.incrementAndGet();
        Page page = new Page(id, spaceId, title, content, 1);
        pages.put(id, page);
        revisionsByPage.put(id, new ArrayList<>(List.of(new Revision(
                revisionIdGenerator.incrementAndGet(),
                id,
                1,
                content
        ))));
        return page;
    }

    public Page updatePage(Long pageId, String content) {
        Page page = pages.get(pageId);
        if (page == null) {
            throw new IllegalArgumentException("page not found");
        }
        if (content == null) {
            throw new IllegalArgumentException("content is required");
        }

        int nextVersion = page.version() + 1;
        Page updated = new Page(page.id(), page.spaceId(), page.title(), content, nextVersion);
        pages.put(pageId, updated);

        revisionsByPage.computeIfAbsent(pageId, ignored -> new ArrayList<>())
                .add(new Revision(revisionIdGenerator.incrementAndGet(), pageId, nextVersion, content));

        return updated;
    }

    public List<Revision> listRevisions(Long pageId) {
        if (!pages.containsKey(pageId)) {
            throw new IllegalArgumentException("page not found");
        }
        return new ArrayList<>(revisionsByPage.getOrDefault(pageId, List.of()));
    }

    public record Space(Long id, String name) {
    }

    public record Page(Long id, Long spaceId, String title, String content, Integer version) {
    }

    public record Revision(Long id, Long pageId, Integer version, String content) {
    }
}
