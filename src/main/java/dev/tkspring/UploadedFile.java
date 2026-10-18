package dev.tkspring;

import java.io.InputStream;
import java.io.IOException;

import jakarta.servlet.http.Part;

public class UploadedFile {

    Part part;

    public UploadedFile(Part part) {
        this.part = part;
    }

    public Part getPart() {
        return part;
    }

    public String getSubmittedFileName() {
        return part.getSubmittedFileName();
    }

    public long getSize() {
        return part.getSize();
    }

    public String getContentType() {
        return part.getContentType();
    }

    public InputStream getInputStream() throws IOException {
        return part.getInputStream();
    }
}
