package pt.psoft.g1.psoftg1.authormanagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

@Getter
@NoArgsConstructor
public class Bio {
    public static final int BIO_MAX_LENGTH = 4096;

    private String bio;

    public Bio(String bio) {
        setBio(bio);
    }


    public void setBio(String bio) {
        if(bio == null)
            throw new IllegalArgumentException("Bio cannot be null");
        if(bio.isBlank())
            throw new IllegalArgumentException("Bio cannot be blank");
        if(bio.length() > BIO_MAX_LENGTH)
            throw new IllegalArgumentException("Bio has a maximum of 4096 characters");
        this.bio = StringUtilsCustom.sanitizeHtml(bio);
    }

    @Override
    public String toString() {
        return bio;
    }
}
