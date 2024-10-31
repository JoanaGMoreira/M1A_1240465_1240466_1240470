package pt.psoft.g1.psoftg1.shared.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.nio.file.Path;

@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    @NotNull
    @Setter
    @Getter
    private String photoFile;


    public Photo(Path photoPath){
        setPhotoFile(photoPath.toString());
    }
}

