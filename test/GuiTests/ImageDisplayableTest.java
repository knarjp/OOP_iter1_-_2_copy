package GuiTests;

import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDisplayableTest
{
    private static final int WHITE_ARGB = Color.WHITE.getRGB();
    private static final int BLACK_ARGB = Color.BLACK.getRGB();

    @Test
    public void testGetOrigin()
    {
        Point origin = new Point(10, 10);
        Displayable displayable = new ImageDisplayable(origin, new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        Assert.assertEquals(origin, displayable.getOrigin());
    }

    @Test
    public void testGetSize()
    {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Displayable displayable = new ImageDisplayable(new Point(1, 1), image);
        Dimension size = displayable.getSize();

        Assert.assertTrue(image.getWidth() == size.getHeight());
        Assert.assertTrue(image.getHeight() == size.getHeight());
    }

    @Test
    public void testDefaultUpdateAndExpiry()
    {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Displayable displayable = new ImageDisplayable(new Point(0, 0), image);
        displayable.update();

        Assert.assertFalse(displayable.expired());
    }

    @Test
    public void testDrawing()
    {
        BufferedImage canvas = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);

        BufferedImage displayImage = new BufferedImage(25, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = displayImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, displayImage.getWidth(), displayImage.getHeight());
        Displayable displayable = new ImageDisplayable(new Point(0, 0), displayImage);

        g2d = canvas.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Assert.assertTrue(canvas.getRGB(12, 12) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(12, 190) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 12) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 190) == BLACK_ARGB);

        displayable.drawAt(g2d, new Point(175, 175));

        Assert.assertTrue(canvas.getRGB(12, 12) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(12, 190) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 12) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 190) == WHITE_ARGB);

        displayable.draw(g2d);

        Assert.assertTrue(canvas.getRGB(12, 12) == WHITE_ARGB);
        Assert.assertTrue(canvas.getRGB(12, 190) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 12) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 190) == WHITE_ARGB);

        displayable.drawWithOffset(g2d, new Point(175, 0));

        Assert.assertTrue(canvas.getRGB(12, 12) == WHITE_ARGB);
        Assert.assertTrue(canvas.getRGB(12, 190) == BLACK_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 12) == WHITE_ARGB);
        Assert.assertTrue(canvas.getRGB(190, 190) == WHITE_ARGB);
    }

    @Test
    public void testGetSetImage()
    {
        BufferedImage initialImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        BufferedImage nextImage = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);

        Assert.assertNotEquals(initialImage, nextImage);

        ImageDisplayable displayable = new ImageDisplayable(new Point(0, 0), initialImage);

        Assert.assertEquals(initialImage, displayable.getImage());

        Dimension size = displayable.getSize();

        Assert.assertTrue(initialImage.getWidth() == size.getWidth());
        Assert.assertTrue(initialImage.getHeight() == size.getHeight());

        displayable.setImage(nextImage);

        Assert.assertEquals(nextImage, displayable.getImage());

        size = displayable.getSize();

        Assert.assertTrue(nextImage.getWidth() == size.getWidth());
        Assert.assertTrue(nextImage.getHeight() == size.getHeight());
    }
}
