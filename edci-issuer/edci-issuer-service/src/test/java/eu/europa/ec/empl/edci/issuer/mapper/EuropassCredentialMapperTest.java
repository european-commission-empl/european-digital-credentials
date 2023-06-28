package eu.europa.ec.empl.edci.issuer.mapper;

public class EuropassCredentialMapperTest /*extends AbstractBaseTest*/ {
//    EuropassCredentialMapper europassCredentialMapper = Mappers.getMapper(EuropassCredentialMapper.class);
//    EuropassCredentialSpecDAO credentialSpecDAO;
//
//    void initCredential() {
//        credentialSpecDAO = new EuropassCredentialSpecDAO();
//
//        CodeDTDAO codeDTDAO = new CodeDTDAO();
//        codeDTDAO.setCodeId("codeId");
//        codeDTDAO.setContent("content");
//        codeDTDAO.setListId("listId");
//        codeDTDAO.setName("name");
//        credentialSpecDAO.setType(codeDTDAO);
//        Date issuanceDate = new Date(5215236);
//        Date expirationDate = new Date(1125125);
//        credentialSpecDAO.setIssuanceDate(issuanceDate);
//        credentialSpecDAO.setExpirationDate(expirationDate);
//
//        Set<LearningAchievementSpecDAO> achievements = new HashSet<>();
//        LearningAchievementSpecDAO learningAchievementSpecDAO = new LearningAchievementSpecDAO();
//        TextDTDAO titleAchievement = new TextDTDAO();
//        List<ContentDTDAO> contentsAchievement = new ArrayList<>();
//        ContentDTDAO contentAchievement = new ContentDTDAO();
//        contentAchievement.setLanguage("language");
//        contentAchievement.setContent("content");
//        contentAchievement.setFormat("format");
//        contentsAchievement.add(contentAchievement);
//        titleAchievement.setContents(contentsAchievement);
//        learningAchievementSpecDAO.setTitle(titleAchievement);
//        achievements.add(learningAchievementSpecDAO);
//        credentialSpecDAO.setAchieved(achievements);
//
//    }
//
//    //@Test
//    public void toDTO_shouldMapTitle() {
//
//        initCredential();
//        TextDTDAO textDTDAO = new TextDTDAO();
//        ContentDTDAO contentOne = new ContentDTDAO();
//        contentOne.setContent("content 1");
//        contentOne.setFormat("format1");
//        contentOne.setLanguage("en_US");
//        ContentDTDAO contentTwo = new ContentDTDAO();
//        contentTwo.setContent("content 2");
//        contentTwo.setFormat("format2");
//        contentTwo.setLanguage("fr_FR");
//        List<ContentDTDAO> contents = new ArrayList<>();
//        contents.add(contentOne);
//        contents.add(contentTwo);
//        textDTDAO.setContents(contents);
//        credentialSpecDAO.setTitle(textDTDAO);
//
//        EuropassCredentialDTO credentialDTO = europassCredentialMapper.toDTO(credentialSpecDAO);
//
//        Assert.assertTrue(credentialDTO.getTitle().getContent("en_US").getContent().equals(contentOne.getContent()));
//        Assert.assertFalse(credentialDTO.getTitle().getContent("fr_FR").getContent().equals(contentOne.getContent()));
//        Assert.assertFalse(credentialDTO.getTitle().getContent("en_US").getContent().equals(contentTwo.getContent()));
//
//        Assert.assertTrue(credentialDTO.getTitle().getContent("en_US").getLanguage().equals(contentOne.getLanguage()));
//        Assert.assertFalse(credentialDTO.getTitle().getContent("fr_FR").getLanguage().equals(contentOne.getLanguage()));
//        Assert.assertFalse(credentialDTO.getTitle().getContent("en_US").getLanguage().equals(contentTwo.getLanguage()));
//
//        Assert.assertTrue(credentialDTO.getTitle().getContent("en_US").getFormat().equals(contentOne.getFormat()));
//        Assert.assertFalse(credentialDTO.getTitle().getContent("fr_FR").getFormat().equals(contentOne.getFormat()));
//        Assert.assertFalse(credentialDTO.getTitle().getContent("en_US").getFormat().equals(contentTwo.getFormat()));
//
//    }
//
//    //@Test
//    public void toDTO_shouldMapDescription() {
//
//        initCredential();
//        NoteDTDAO noteDTDAO = new NoteDTDAO();
//        ContentDTDAO contentOne = new ContentDTDAO();
//        contentOne.setContent("content 1");
//        contentOne.setFormat("format1");
//        contentOne.setLanguage("en_US");
//        ContentDTDAO contentTwo = new ContentDTDAO();
//        contentTwo.setContent("content 2");
//        contentTwo.setFormat("format2");
//        contentTwo.setLanguage("fr_FR");
//        List<ContentDTDAO> contents = new ArrayList<>();
//        contents.add(contentOne);
//        contents.add(contentTwo);
//        noteDTDAO.setContents(contents);
//        noteDTDAO.setTopic("topic");
//        credentialSpecDAO.setDescription(noteDTDAO);
//
//        EuropassCredentialDTO credentialDTO = europassCredentialMapper.toDTO(credentialSpecDAO);
//
//        Assert.assertTrue(credentialDTO.getDescription().getContent("en_US").getContent().equals(contentOne.getContent()));
//        Assert.assertFalse(credentialDTO.getDescription().getContent("fr_FR").getContent().equals(contentOne.getContent()));
//        Assert.assertFalse(credentialDTO.getDescription().getContent("en_US").getContent().equals(contentTwo.getContent()));
//
//        Assert.assertTrue(credentialDTO.getDescription().getContent("en_US").getLanguage().equals(contentOne.getLanguage()));
//        Assert.assertFalse(credentialDTO.getDescription().getContent("fr_FR").getLanguage().equals(contentOne.getLanguage()));
//        Assert.assertFalse(credentialDTO.getDescription().getContent("en_US").getLanguage().equals(contentTwo.getLanguage()));
//
//        Assert.assertTrue(credentialDTO.getDescription().getContent("en_US").getFormat().equals(contentOne.getFormat()));
//        Assert.assertFalse(credentialDTO.getDescription().getContent("fr_FR").getFormat().equals(contentOne.getFormat()));
//        Assert.assertFalse(credentialDTO.getDescription().getContent("en_US").getFormat().equals(contentTwo.getFormat()));
//
//        Assert.assertTrue(credentialDTO.getDescription().getTopic().equals(noteDTDAO.getTopic()));
//
//    }
//
//    //@Test
//    public void toDTO_shouldMapType() {
//
//        initCredential();
//
//        EuropassCredentialDTO credentialDTO = europassCredentialMapper.toDTO(credentialSpecDAO);
//
//        Assert.assertTrue(credentialDTO.getType().getCodeId().equals(credentialSpecDAO.getType().getCodeId()));
//        Assert.assertTrue(credentialDTO.getType().getContent().equals(credentialSpecDAO.getType().getContent()));
//        Assert.assertTrue(credentialDTO.getType().getListId().equals(credentialSpecDAO.getType().getListId()));
//        Assert.assertTrue(credentialDTO.getType().getName().equals(credentialSpecDAO.getType().getName()));
//
//    }
//
//    //@Test
//    public void toDTO_shouldMapIssuanceDate() {
//
//        initCredential();
//
//        EuropassCredentialDTO credentialDTO = europassCredentialMapper.toDTO(credentialSpecDAO);
//
//        Assert.assertTrue(credentialDTO.getIssuanceDate().equals(credentialSpecDAO.getIssuanceDate()));
//
//    }
//
//    //@Test
//    public void toDTO_shouldMapExpirationDate() {
//
//        initCredential();
//
//        EuropassCredentialDTO credentialDTO = europassCredentialMapper.toDTO(credentialSpecDAO);
//
//        Assert.assertTrue(credentialDTO.getExpirationDate().equals(credentialSpecDAO.getExpirationDate()));
//
//    }

    //@Test
    public void toDTO_shouldMapPersonAchieved() {

//        initCredential();
//
//
//        EuropassCredentialDTO credentialDTO = europassCredentialMapper.toDTO(credentialSpecDAO, "en_US");
//
//        Assert.assertTrue(credentialDTO.getCredentialSubject().getAchieved().get(0).getTitle().getContent("language").getContent().equals("content"));


//        EuropassCredentialDTO credentialDTO = europassCredentialMapper.toDTO(credentialSpecDAO);
//
//        Assert.assertTrue(credentialDTO.getCredentialSubject().getAchieved().get(0).getTitle().getContent("language").getContent().equals("content"));

    }
}