package csci432;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class SigmaDeltaFilter {

    public int numFiltered;
    public BufferedImage background;
    protected int[][] backCount, curPix, curCount;
    public int colorThresh = 10;
    public int initBackground = 5;     //amount of pictures to be taken to initialize background

    /**
     * Initializes SigmaDelta Filter
     **/
    public SigmaDeltaFilter() {
        this.numFiltered = 0;
        /*backCount = new int[700][700];
        curCount = new int[700][700];
        curPix = new int[700][700];*/
    }

    /**
     * Decides either to filter image, or just set the background using
     * a threshold for images processed so far.
     *
     * @param image a BufferedImage to be filtered
     * @return the newly filtered image if numFiltered is more than
     * 20 or just the original image if numFiltered is 20 or less
     **/
    public BufferedImage filter(BufferedImage image) {
        if (numFiltered > initBackground) {
            refreshBackground(image);
            image = filterImageSubtract(image);
        } else if (numFiltered > 0) {
            refreshBackground(image);
        } else {
            background = image;
            curCount = new int[image.getWidth()][image.getHeight()];
            curPix = new int[image.getWidth()][image.getHeight()];
            backCount = new int[image.getWidth()][image.getHeight()];
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    curPix[x][y] = image.getRGB(x, y);
                }
            }
        }
        numFiltered++;
        return image;
    }

    /**
     * For each pixel in the inputted image
     *
     * @param image a BufferedImage to be filtered
     **/
    public void refreshBackground(BufferedImage image) {
        Color backColor, curColor, imageColor = null;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                backColor = new Color(background.getRGB(x, y));
                imageColor = new Color(image.getRGB(x, y));
                if (colorMatch(backColor, imageColor)) {
                    backCount[x][y]++;
                } else {
                    curColor = new Color(curPix[x][y]);
                    if (colorMatch(curColor, imageColor)) {
                        curCount[x][y]++;
                        if (curCount[x][y] > backCount[x][y]) {
                            background.setRGB(x, y, rgb);
                        }
                    } else {
                        curPix[x][y] = rgb;
                        curCount[x][y] = 0;
                    }
                }
            }
        }
    }

    public boolean colorMatch(Color a, Color b) {
        int aRed = a.getRed();
        int aGreen = a.getGreen();
        int aBlue = a.getBlue();
        int bRed = b.getRed();
        int bGreen = b.getGreen();
        int bBlue = b.getBlue();

        if (Math.abs(aRed - bRed) < colorThresh && Math.abs(aGreen - bGreen)
                < colorThresh && Math.abs(aBlue - bBlue) < colorThresh) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calls refreshBackground() then for each pixel in image,
     * subtracts the corresponding RGB value in background from
     * the pixel in image. If the new value is negative, it is set
     * to positive before being assigned to the pixel. Since both
     * filter methods are called, refreshBackground() is commented
     * out so the background isn't averaged twice. We will decide
     * on one later
     *
     * @param image a BufferedImage to be filtered
     * @return the newly filtered image
     **/
    public BufferedImage filterImageSubtract(BufferedImage image) {
        Color bColor = null;
        Color iColor = null;
        int rgb = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                bColor = new Color(background.getRGB(j, i));
                iColor = new Color(image.getRGB(j, i));
                rgb = subColor(bColor, iColor);
                image.setRGB(j, i, rgb);
            }
        }
        return image;
    }

    public int subColor(Color a, Color b) {
        int aRed = a.getRed();
        int aGreen = a.getGreen();
        int aBlue = a.getBlue();
        int bRed = b.getRed();
        int bGreen = b.getGreen();
        int bBlue = b.getBlue();

        aRed = Math.abs(aRed - bRed);
        aGreen = Math.abs(aGreen - bGreen);
        aBlue = Math.abs(aBlue - bBlue);
        int rgb = (new Color(aRed, aGreen, aBlue)).getRGB();
        return rgb;
    }
}
