package random.gba.randomizer;

import org.junit.jupiter.api.Test;

import java.util.Random;

import fedata.gba.GBAFEClassData;
import fedata.gba.fe8.FE8Class;
import random.gba.loader.ClassDataLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClassRandomizerTest {

    @Test
    void randomizeClassMovement_includesMaximum() {
        // Arrange
        int minimum = 6;
        int maximum = 6;
        ClassDataLoader mockLoader = loaderWithOneClass();
        Random rng = new Random();

        // Act
        ClassRandomizer.randomizeClassMovement(minimum, maximum, mockLoader, rng);

        // Assert
        assertEquals(6, mockLoader.allClasses()[0].getMOV());
    }

    @Test
    void randomizeClassMovement_withinRange() {
        // Arrange
        int minimum = 6;
        int maximum = 9;
        ClassDataLoader mockLoader = loaderWithOneClass();
        Random rng = new Random();

        // Act
        ClassRandomizer.randomizeClassMovement(minimum, maximum, mockLoader, rng);

        // Assert
        int actual = mockLoader.allClasses()[0].getMOV();
        assertTrue(6 <= actual);
        assertTrue(actual <= 9);
    }

    @Test
    void randomizeClassMovement_remainsZero() {
        // Arrange
        int minimum = 6;
        int maximum = 9;
        ClassDataLoader mockLoader = loaderWithOneClass();
        mockLoader.allClasses()[0].setMOV(0);
        Random rng = new Random();

        // Act
        ClassRandomizer.randomizeClassMovement(minimum, maximum, mockLoader, rng);

        // Assert
        assertEquals(0, mockLoader.allClasses()[0].getMOV());
    }

    private ClassDataLoader loaderWithOneClass() {
        ClassDataLoader mockClassDataLoader = mock(ClassDataLoader.class);
        when(mockClassDataLoader.allClasses()).thenReturn(classDataArrayWithOneClass());
        return mockClassDataLoader;
    }

    private GBAFEClassData[] classDataArrayWithOneClass() {
        GBAFEClassData[] array = new GBAFEClassData[1];
        array[0] = classDataWithMovement();
        return array;
    }

    private GBAFEClassData classDataWithMovement() {
        byte[] bytes = new byte[99];
        GBAFEClassData classData = new FE8Class(bytes, 0);
        classData.setMOV(4);
        return classData;
    }
}
