package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.BookClubMembersId;
import app.SocialLibraryAPI.entity.BookClubMembersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BookClubMembersRepository extends JpaRepository<BookClubMembersEntity, BookClubMembersId> {
    List<BookClubMembersEntity> findByBookClub_Id(Integer clubId);
}