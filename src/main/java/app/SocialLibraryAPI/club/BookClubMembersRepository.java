package app.SocialLibraryAPI.club;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BookClubMembersRepository extends JpaRepository<BookClubMembersEntity, BookClubMembersIdEntity> {
    List<BookClubMembersEntity> findByBookClub_Id(Integer clubId);
}