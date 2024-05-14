package com.google.storyapp.utils

import com.google.storyapp.remote.response.Story

object DataDummy {

    fun dummyStories(): List<Story> {
        return listOf(
            Story(
                id = "1",
                name = "Story 1",
                description = "Description 1",
                photoUrl = "https://example.com/photo1.jpg",
                createdAt = "2023-05-07T10:00:00Z",
                lat = -6.1234,
                lon = 106.5678
            ),
            Story(
                id = "2",
                name = "Story 2",
                description = "Description 2",
                photoUrl = "https://example.com/photo2.jpg",
                createdAt = "2023-05-07T11:00:00Z",
                lat = -6.9012,
                lon = 107.3456
            ),
            Story(
                id = "3",
                name = "Story 3",
                description = "Description 3",
                photoUrl = "https://example.com/photo3.jpg",
                createdAt = "2023-05-07T12:00:00Z",
                lat = -6.7890,
                lon = 108.1234
            )
        )
    }
}