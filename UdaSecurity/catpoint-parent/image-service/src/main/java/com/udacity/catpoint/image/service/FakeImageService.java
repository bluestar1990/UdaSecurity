package com.udacity.catpoint.image.service;

import java.awt.image.BufferedImage;
import java.util.Random;

public class FakeImageService implements ImageService{
    private final Random r = new Random();

    public boolean imageContainsCat(BufferedImage image, float confidenceThreshhold) {
        return r.nextBoolean();
    }
}
