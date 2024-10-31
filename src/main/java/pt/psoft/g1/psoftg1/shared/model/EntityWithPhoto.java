package pt.psoft.g1.psoftg1.shared.model;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@Getter
@Setter
public abstract class EntityWithPhoto {
    @Nullable
    protected Photo photo;


    //This method is used by the mapper in order to set the photo. This will call the setPhotoInternal method that
    //will contain all the logic to set the photo
    public void setPhotoUri(String photoUri) {
        this.setPhotoInternal(photoUri);
    }

    protected void setPhotoInternal(String photoURI) {
        if (photoURI == null) {
            this.photo = null;
        } else {
            try {
                //If the Path object instantiation succeeds, it means that we have a valid Path
                this.photo = new Photo(Path.of(photoURI));
            } catch (InvalidPathException e) {
                //For some reason it failed, let's set to null to avoid invalid references to photos
                this.photo = null;
            }
        }
    }
}
