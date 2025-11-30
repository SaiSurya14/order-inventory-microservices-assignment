package com.koerber.assignment.inventory.strategy;

import com.koerber.assignment.inventory.entity.ProductType;
import com.koerber.assignment.inventory.exception.InvalidStrategyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryStrategyFactoryTest {

    @Mock
    private InventoryStrategy foodStrategy;

    @Mock
    private InventoryStrategy electronicsStrategy;

    @Test
    void getStrategy_shouldReturnCorrectStrategy_whenTypeMatches() {
        // Arrange
        when(foodStrategy.getSupportedType()).thenReturn(ProductType.FOOD);

        InventoryStrategyFactory factory =
                new InventoryStrategyFactory(List.of(foodStrategy));

        // Act
        InventoryStrategy result = factory.getStrategy("FOOD");

        // Assert
        assertSame(foodStrategy, result);
        verify(foodStrategy, times(1)).getSupportedType();
    }

    @Test
    void getStrategy_shouldThrowException_whenStrategyNotFound() {
        // Arrange
        when(foodStrategy.getSupportedType()).thenReturn(ProductType.FOOD);

        InventoryStrategyFactory factory =
                new InventoryStrategyFactory(List.of(foodStrategy));

        // Act + Assert
        InvalidStrategyException ex = assertThrows(
                InvalidStrategyException.class,
                () -> factory.getStrategy("TOY")
        );

        assertEquals("No strategy found for type: TOY", ex.getMessage());
        verify(foodStrategy, times(1)).getSupportedType();
    }
}