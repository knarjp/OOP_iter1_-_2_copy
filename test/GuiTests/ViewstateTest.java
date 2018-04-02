package GuiTests;

import com.MeanTeam.guiframework.Viewstate;
import com.MeanTeam.guiframework.displayables.Displayable;
import com.MeanTeam.guiframework.displayables.ImageDisplayable;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class ViewstateTest
{
    @Test
    public void drawingTest()
    {
        BufferedImage redImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = redImage.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, redImage.getWidth(), redImage.getHeight());
        Displayable redDisplayable = new ImageDisplayable(new Point(0, 0), redImage);

        BufferedImage blueImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        g2d = blueImage.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, blueImage.getWidth(), blueImage.getHeight());
        Displayable blueDisplayable = new ImageDisplayable(new Point(redImage.getWidth(), 0), blueImage);

        BufferedImage canvas = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int redARGB = Color.RED.getRGB();
        int blueARGB = Color.BLUE.getRGB();
        int blackARGB = Color.BLACK.getRGB();

        Assert.assertEquals(redARGB, redImage.getRGB(25, 25));
        Assert.assertEquals(blueARGB, blueImage.getRGB(25, 25));

        Assert.assertEquals(blackARGB, canvas.getRGB(25, 25));
        Assert.assertEquals(blackARGB, canvas.getRGB(75, 25));
        Assert.assertEquals(blackARGB, canvas.getRGB(450, 450));

        Set<Displayable> displayables = new HashSet<>();
        displayables.add(redDisplayable);
        displayables.add(blueDisplayable);

        Viewstate viewstate = new Viewstate();

        viewstate.add(displayables);

        viewstate.draw(g2d);

        Assert.assertEquals(redARGB, canvas.getRGB(25, 25));
        Assert.assertEquals(blueARGB, canvas.getRGB(75, 25));
        Assert.assertEquals(blackARGB, canvas.getRGB(450, 450));
    }

    @Test
    public void updatingTest()
    {
        class UpdateCounterDisplayable implements Displayable
        {
            private int counter = 0;
            public Point getOrigin() {return new Point(0, 0);}
            public Dimension getSize() {return new Dimension(1, 1);}
            public void drawAt(Graphics2D g2d, Point drawPt) {}
            public void update() { ++counter; }
        }

        UpdateCounterDisplayable displayable1 = new UpdateCounterDisplayable();
        UpdateCounterDisplayable displayable2 = new UpdateCounterDisplayable();
        Set<Displayable> displayables = new HashSet<>();
        displayables.add(displayable1);
        displayables.add(displayable2);

        Viewstate viewstate = new Viewstate();
        viewstate.add(displayables);

        Assert.assertEquals(0, displayable1.counter);
        Assert.assertEquals(0, displayable2.counter);

        viewstate.update();

        Assert.assertEquals(1, displayable1.counter);
        Assert.assertEquals(1, displayable2.counter);
    }
}
