package GuiTests;

import com.MeanTeam.guiframework.InterfacePanel;
import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.displayables.CompositeDisplayable;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CompositeDisplayableTest
{
    @Test
    public void testGetOrigin()
    {
        Point origin = new Point(10, 10);
        Displayable displayable = new CompositeDisplayable(origin);

        Assert.assertEquals(origin, displayable.getOrigin());
    }

    @Test
    public void testGetSize()
    {
        Dimension expectedSize = new Dimension(10, 10);
        Displayable component = new ImageDisplayable(new Point(0, 0),
                new BufferedImage((int) expectedSize.getWidth(), (int) expectedSize.getHeight(), BufferedImage.TYPE_INT_ARGB));

        CompositeDisplayable composite = new CompositeDisplayable(new Point(0, 0));

        Assert.assertNotEquals(expectedSize, composite.getSize());

        composite.add(component);

        Assert.assertEquals(expectedSize, composite.getSize());

        Displayable component2 = new ImageDisplayable(new Point(expectedSize.width, expectedSize.height),
                new BufferedImage((int) expectedSize.getWidth(), (int) expectedSize.getHeight(), BufferedImage.TYPE_INT_ARGB));

        composite.add(component2);

        Assert.assertNotEquals(expectedSize, composite.getSize());

        expectedSize.setSize(expectedSize.width * 2, expectedSize.height * 2);

        Assert.assertEquals(expectedSize, composite.getSize());

        composite.remove(component2);

        Assert.assertNotEquals(expectedSize, composite.getSize());

        expectedSize.setSize(expectedSize.width / 2, expectedSize.height / 2);

        Assert.assertEquals(expectedSize, composite.getSize());
    }

    @Test
    public void testUpdating()
    {
        class UpdateCounterDisplayable implements Displayable
        {
            private int counter = 0;
            public Point getOrigin() {return new Point(0, 0);}
            public Dimension getSize() {return new Dimension(1, 1);}
            public void drawAt(Graphics2D g2d, Point drawPt) {}
            public void update() { ++counter; }
        }

        UpdateCounterDisplayable component1 = new UpdateCounterDisplayable();
        UpdateCounterDisplayable component2 = new UpdateCounterDisplayable();
        CompositeDisplayable composite = new CompositeDisplayable(new Point(0, 0), component1);
        composite.add(component2);

        Assert.assertEquals(0, component1.counter);
        Assert.assertEquals(0, component2.counter);

        composite.update();

        Assert.assertEquals(1, component1.counter);
        Assert.assertEquals(1, component2.counter);
    }

    @Test
    public void testDrawing()
    {
        BufferedImage redImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = redImage.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0 ,0, redImage.getWidth(), redImage.getHeight());
        Displayable redDisplayable = new ImageDisplayable(new Point(0,0), redImage);

        BufferedImage blueImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        g2d = blueImage.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0 ,0, blueImage.getWidth(), blueImage.getHeight());
        Displayable blueDisplayable = new ImageDisplayable(new Point(redImage.getWidth(), 0), blueImage);

        BufferedImage canvas = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int redARGB = Color.RED.getRGB();
        int blueARGB = Color.BLUE.getRGB();
        int blackARGB = Color.BLACK.getRGB();

        Assert.assertEquals(redImage.getRGB(25, 25), redARGB);
        Assert.assertEquals(blueImage.getRGB(25, 25), blueARGB);
        Assert.assertEquals(canvas.getRGB(250, 250), blackARGB);

        CompositeDisplayable composite = new CompositeDisplayable(new Point(0, 0), redDisplayable);
        composite.add(blueDisplayable);

        composite.drawAt(g2d, new Point(200, 200));

        Assert.assertEquals(canvas.getRGB(25, 25), blackARGB);
        Assert.assertEquals(canvas.getRGB(75, 25), blackARGB);
        Assert.assertEquals(canvas.getRGB(225, 225), redARGB);
        Assert.assertEquals(canvas.getRGB(275, 225), blueARGB);
        Assert.assertEquals(canvas.getRGB(325, 325), blackARGB);
        Assert.assertEquals(canvas.getRGB(375, 325), blackARGB);

        composite.draw(g2d);

        Assert.assertEquals(canvas.getRGB(25, 25), redARGB);
        Assert.assertEquals(canvas.getRGB(75, 25), blueARGB);
        Assert.assertEquals(canvas.getRGB(225, 225), redARGB);
        Assert.assertEquals(canvas.getRGB(275, 225), blueARGB);
        Assert.assertEquals(canvas.getRGB(325, 325), blackARGB);
        Assert.assertEquals(canvas.getRGB(375, 325), blackARGB);

        composite.drawWithOffset(g2d, new Point(300, 300));

        Assert.assertEquals(canvas.getRGB(25, 25), redARGB);
        Assert.assertEquals(canvas.getRGB(75, 25), blueARGB);
        Assert.assertEquals(canvas.getRGB(225, 225), redARGB);
        Assert.assertEquals(canvas.getRGB(275, 225), blueARGB);
        Assert.assertEquals(canvas.getRGB(325, 325), redARGB);
        Assert.assertEquals(canvas.getRGB(375, 325), blueARGB);
    }
}
