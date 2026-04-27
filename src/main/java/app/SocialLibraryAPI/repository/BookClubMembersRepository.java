package app.SocialLibraryAPI.repository;

import app.SocialLibraryAPI.entity.BookClubMembersId;
import app.SocialLibraryAPI.entity.Book_Club_Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookClubMembersRepository extends JpaRepository<Book_Club_Members, BookClubMembersId> {
}