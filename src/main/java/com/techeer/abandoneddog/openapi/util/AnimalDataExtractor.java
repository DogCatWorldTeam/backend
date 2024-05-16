package com.techeer.abandoneddog.openapi.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.techeer.abandoneddog.animal.entity.*;
import com.techeer.abandoneddog.shelter.entity.Shelter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimalDataExtractor {

    public static void processAndDeleteResponseFile(String response) {
        try {
            Path path = Path.of("animals_response.json");
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Response file deleted successfully.");
            }
            Files.writeString(path, response, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to delete or write response file.");
        }
    }

    public static PetInfo buildPetInfoFromJson(JsonNode item, DateTimeFormatter formatter, Shelter shelter) {
        String kindCode = item.path("kindCd").asText();
        String extractedPetType = extractPetType(kindCode);
        String breed = kindCode.replaceAll("\\[.*?\\] ", "");

        return PetInfo.builder()
                .shelter(shelter)
                .desertionNo(item.path("desertionNo").asText())
                .thumbnailImage(item.path("filename").asText())
                .happenDt(LocalDate.parse(item.path("happenDt").asText(), formatter))
                .happenPlace(item.path("happenPlace").asText())
                .breed(breed)
                .petType(PetType.valueOf(extractedPetType.toUpperCase().replaceAll("\\s", "")))
                .color(item.path("colorCd").asText())
                .age(extractYearFromAge(item.path("age").asText()))
                .weight(extractWeight(item.path("weight").asText()))
                .noticeNo(item.path("noticeNo").asText())
                .noticeSdt(LocalDate.parse(item.path("noticeSdt").asText(), formatter))
                .noticeEdt(LocalDate.parse(item.path("noticeEdt").asText(), formatter))
                .profileImage(item.path("popfile").asText())
                .processState(State.fromDescription(item.path("processState").asText()))
                .gender(Gender.valueOf(convertSexCdToGender(item.path("sexCd").asText())))
                .neuter(Neuter.valueOf(convertNeuterYnToNeuter(item.path("neuterYn").asText())))
                .specialMark(item.path("specialMark").asText())
                .orgNm(item.path("orgNm").asText())
                .chargeNm(item.path("chargeNm").asText())
                .officetel(item.path("officetel").asText())
                .isPublicApi(true)
                .build();
    }

    private static String extractPetType(String kindCd) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(kindCd);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static int extractYearFromAge(String age) {
        return Integer.parseInt(age.substring(0, 4));
    }

    private static float extractWeight(String weight) {
        try {
            String cleanWeight = weight.replaceAll("[^\\d.]", "");
            return Float.parseFloat(cleanWeight);
        } catch (NumberFormatException e) {
            System.err.println("Invalid weight format: " + weight);
            return 0;
        }
    }
    private static String convertSexCdToGender(String sexCd) {
        switch (sexCd) {
            case "M":
                return "Male";
            case "F":
                return "Female";
            default:
                return "Unknown";
        }
    }

    private static String convertNeuterYnToNeuter(String neuterYn) {
        switch (neuterYn) {
            case "Y":
                return "YES";
            case "N":
                return "NO";
            default:
                return "UNKNOWN";
        }
    }
}

