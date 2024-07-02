//-------------------------- Class to store the details of the product -------------------------
class Product
{
    int id;
    String name;
    double price;
    String description;
    int stock;

    //---------------------- Constructor to intitialize the data members -------------------------
    public Product(int id, String name, double price, String description, int stock)
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    //---------------------- Function to display the details of the product -------------------------
    public String displayDetail()
    {
        String detail = Integer.toString(id)+")  "+name+"  ("+description+")  "+" Rs."+price;
        return detail;
    }

    //---------------- Function to check if the stock is available for desired quantity ------------------
    public boolean isAvailable(int quantity)
    {
        return quantity <= stock;
    }
}