package ch.epfl.javelo.projection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import ch.epfl.javelo.projection.Ch1903;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointWebMercatorTest {
    @Test
    public void constructorThrowsOnNonValidCoordinates(){
        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(-1, 0.2);
        });
        assertThrows(IllegalArgumentException.class,()->{
            new PointWebMercator(0.2,-1);
        });
        assertThrows(IllegalArgumentException.class,()->{
           new PointWebMercator(2,0.2);
        });
        assertThrows(IllegalArgumentException.class,()->{
            new PointWebMercator(0.2,2);
        });
    }
    @Test
    public void constructorWorksOnTrivialValue(){
       var actual1 = new PointWebMercator(0.2,0.2);
       var expectedx1 =0.2;
       var expectedy1 =0.2;
       assertEquals(expectedx1,actual1.x());
       assertEquals(expectedy1,actual1.y());

       var actual2 = new PointWebMercator(0,0);
       var expectedx2=0;
       var expectedy2 =0;
       assertEquals(expectedx2, actual2.x());
       assertEquals(expectedy2, actual2.y());

        var expectedx3=1;
        var expectedy3 =1;
        var actual3 = new PointWebMercator(1,1);
        assertEquals(expectedx3, actual3.x());
        assertEquals(expectedy3, actual3.y());

    }
    @Test
    public void pointWebMercatorOfWorksOnTrivialValue(){
        var expectedx = 0.518275214444;
        var expectedy = 0.353664894749;
        assertEquals(expectedx, (PointWebMercator.of(19,69561722,47468099)).x());
        assertEquals(expectedy, (PointWebMercator.of(19,69561722,47468099)).y());
    }
    @Test
    public void ofPointChWorksOnKnownValue(){
        var expectedx = 0.518275214444;
        var expectedy = 0.353664894749;

        double e = Ch1903.e(Math.toRadians(6.5790772),Math.toRadians(46.5218976));
        double n = Ch1903.n(Math.toRadians(6.5790772), Math.toRadians(46.5218976));

        var actual = new PointCh(e,n);
        assertEquals(expectedx, (PointWebMercator.ofPointCh(actual)).x());
        assertEquals(expectedy, (PointWebMercator.ofPointCh(actual)).y());
    }
    @Test
    public void xAtZoomLevelWorksOnTrivialValue(){
        var expectedx = 69561722;
        var actual = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(expectedx, actual.xAtZoomLevel(19));

    }
    @Test
    public void yAtZoomLevelWorksOnTrivialValue(){
        var expectedy = 47468099;
        var actual = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(expectedy, actual.yAtZoomLevel(19));
    }
    @Test
    public void lonWorksOnKnownValue(){
        var expected1= Math.toRadians(6.5790772);
        var actual = new PointWebMercator(0.518275214444,0.353664894749);
        System.out.println(actual);



        //var x = 0.518275214444;
        //var l = 2 * Math.PI * x  - Math.PI;
        //System.out.println(l);
       assertEquals(expected1, actual.lon());
      //  assertEquals(l, WebMercator.lon(x));
    }
    @Test
    public void latWorksOnKnownValue(){

        var expected2=Math.toRadians(46.5218976);
        var actual = new PointWebMercator(0.518275214444,0.353664894749);


        assertEquals(expected2, actual.lat());
    }
    @Test
    public void toPointChWorksOnKnownValue(){
        var actual = new PointWebMercator(0.518275214444,0.353664894749);
        var expectedx = 0.518275214444;
        var expectedy=0.353664894749;

        assertEquals(expectedx,(actual.toPointCh()).e());
        assertEquals(expectedy,(actual.toPointCh()).n());

        var actual1 = new PointWebMercator(0.8,0.8);


        assertEquals(null, (actual1.toPointCh()).e());
        assertEquals(null, (actual1.toPointCh()).n());

    }

}
