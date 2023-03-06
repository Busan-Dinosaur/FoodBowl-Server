package com.dinosaur.foodbowl.domain.photo.application.file;

import static com.dinosaur.foodbowl.domain.photo.application.file.PhotoFileConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dinosaur.foodbowl.IntegrationTest;
import com.dinosaur.foodbowl.domain.photo.entity.Photo;
import com.dinosaur.foodbowl.domain.post.entity.Post;
import com.dinosaur.foodbowl.global.error.BusinessException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PhotoFileServiceTest extends IntegrationTest {

    @Nested
    class 사진_저장 {

        @Test
        void 사진_파일이_유효하면_저장에_성공한다() throws IOException {
            Post post = postTestHelper.builder().build();
            Photo result = photoFileService.save(photoTestHelper.getImageFile(), post);

            assertThat(result).isNotNull();
            deleteTestFile(result);
        }

        @Test
        void 사진_파일이_유효하지_않으면_예외가_발생한다() throws IOException {
            Post post = postTestHelper.builder().build();

            assertThatThrownBy(
                    () -> photoFileService.save(photoTestHelper.getFakeImageFile(), post)
            ).isInstanceOf(BusinessException.class);

            assertThatThrownBy(() -> photoFileService.save(null, post))
                    .isInstanceOf(BusinessException.class);

            assertThatThrownBy(
                    () -> photoFileService.save(photoTestHelper.getTooLongNameImageFile(448), post)
            ).isInstanceOf(BusinessException.class);
        }

        @Test
        void 존재하지_않는_게시글에_사진을_추가하면_예외가_발생한다() {
            Post deleted = postTestHelper.builder().build();
            postRepository.delete(deleted);
            em.flush();
            em.clear();

            assertThatThrownBy(
                    () -> photoFileService.save(photoTestHelper.getImageFile(), deleted)
            ).isInstanceOf(BusinessException.class);
        }

        private void deleteTestFile(Photo photo) {
            File photoFile = new File(ROOT_PATH + photo.getPath());
            assertThat(photoFile).exists();
            assertThat(photoFile.delete()).isTrue();
        }
    }

    @Nested
    class 사진_삭제 {

        @Test
        void 사진_파일이_존재하면_삭제에_성공한다() throws IOException {
            Photo saved = photoFileService.save(
                    photoTestHelper.getImageFile(),
                    postTestHelper.builder().build()
            );

            photoFileService.delete(saved);

            assertThat(Files.exists(Path.of(ROOT_PATH + saved.getPath()))).isFalse();
        }
    }
}
