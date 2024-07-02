//-------------------------- Class to store details of user's chosen product -------------------------
class Item
{
    Product product;
    int quantity;

    //--------------------- Constructor to initialize the data members -------------------------
    public Item(Product product, int quantity)
    {
        this.product = product;
        this.quantity = quantity;
    }

    //------------------ Function to calculate item price based on the quantity ----------------
    public double getItemPrice()
    {
        return quantity*product.price;
    }

    //-------------------------- Function to display the item information -------------------------
    public String getItemInfo()
    {
        return Integer.toString(product.id)+") "+product.name+" x "+quantity+" = Rs."+getItemPrice();
    }
}