package com.example.scalorie_v1;

public class product {
    private int caloriesTo100;
    private int gram;
    private String name;
    private int cal;
    public product(String name, int calories, int gram)
    {
        this.name = name;
        this.caloriesTo100 = calories;
        this.gram = gram;
        this.cal = (int)((double)caloriesTo100/100 * gram);
    }
    public int getCaloriesTo100()
    {
        return this.caloriesTo100;
    }
    public int getCal() {
        return this.cal;
    }

    public String getName() {
        return this.name;
    }
    public int getGram()
    {
        return this.gram;
    }
}
