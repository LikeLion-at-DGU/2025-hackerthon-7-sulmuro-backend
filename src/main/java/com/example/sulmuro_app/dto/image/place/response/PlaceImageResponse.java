package com.example.sulmuro_app.dto.image.place.response;
import com.example.sulmuro_app.domain.image.PlaceImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceImageResponse {
    private Long imageId;
    private Long placeId;
    private String url;
    private String filename;
    private boolean cover;
    private String createdAt;

    public static PlaceImageResponse of(PlaceImage i, Long coverImageId) {
        return new PlaceImageResponse(
                i.getImageId(),
                i.getPlace().getPlace_id(),
                i.getUrl(),
                i.getFilename(),
                coverImageId != null && coverImageId.equals(i.getImageId()),
                i.getCreatedAt().toString()
        );
    }
}
