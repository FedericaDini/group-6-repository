import beans.Order;
import beans.Product;
import beans.Review;
import beans.User;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.ReviewDAO;
import dao.UserDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Client {
    private BufferedReader inKeyboard;
    private PrintWriter outVideo;

    public Client() {

        //Setting of the variables for IO operations
        prepareIO();

        outVideo.println("Welcome to E-SHOP!");

        //Real execution of the application
        execute();
    }

    private void prepareIO() {
        inKeyboard = new BufferedReader(new InputStreamReader(System.in));
        outVideo = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    private void execute() {
        User user = null;
        while (true) {
            if (user != null) {
                outVideo.println("Hello, " + user.getUsername());
                outVideo.println("What do you want to do?");
                if (user.getType().equals("ADMIN")) {
                    outVideo.println("e --> Add a new employee");
                    outVideo.println("u --> Search users");
                }
                if (user.getType().equals("EMP") || user.getType().equals("ADMIN")) {
                    outVideo.println("a --> Add a new product");
                }
                outVideo.println("s --> Search products");
                if (user.getType().equals("CUST")) {
                    outVideo.println("d --> See details about a suggested product");
                }
                outVideo.println("o --> Show my orders");
                outVideo.println("c --> Go to cart");
                outVideo.println("l --> Log out");
                outVideo.println("q --> Quit");
                outVideo.println();
                outVideo.println();

                outVideo.println("The users that purchaised the same product as you, purchased also the products you can see below.");
                outVideo.println("--- Suggested products ---");

                //Compute the list of suggested products
                HashMap<String, String> suggestedProducts = ProductDAO.findSuggestedProductsByUsername(user.getUsername());
                //Shows all the results of the search
                int limit = showResults(suggestedProducts);

                try {
                    String choice = inKeyboard.readLine();

                    if (choice.equals("e") && user.getType().equals("ADMIN")) {
                        register("EMP");
                    }
                    if (choice.equals("u") && user.getType().equals("ADMIN")) {
                        searchUsers();
                    }
                    if (choice.equals("a") && (user.getType().equals("EMP") || user.getType().equals("ADMIN"))) {
                        addProduct();
                    } else if (choice.equals("d") && user.getType().equals("CUST")) {
                        if (limit != 0) {
                            //Retrieves the ID of the selected product
                            String id = chooseElement(suggestedProducts, limit);

                            //Retrieves the details of the selected product
                            Product p = ProductDAO.findProductById(id);

                            //Prints the details of the selected product
                            outVideo.println(p);

                            manageProduct(p, user);
                        }
                    } else if (choice.equals("s")) {
                        searchProducts(user);
                    } else if (choice.equals("o")) {
                        searchOrders(user);
                    } else if (choice.equals("c")) {
                        retrieveCart(user);
                    } else if (choice.equals("l")) {
                        user = null;
                    } else if (choice.equals("q")) {
                        outVideo.println("Goodbye!");
                        //Close connection to database!!!!
                        break;
                    } else
                        outVideo.println("WRONG INPUT");
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                    e.printStackTrace();
                }

            } else {
                user = authenticate();
            }

        }
    }

    private User authenticate() {
        outVideo.println("Have you already been here? --> press 1 to login");
        outVideo.println("Are you new? --> press 2 to register");

        String authType = null;

        User u = null;

        while (authType == null) {
            try {
                authType = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int action = 0;
            try {
                action = Integer.parseInt(authType);
            } catch (NumberFormatException nfe) {
                authType = null;
                outVideo.println("You can insert only 1 (login) or 2 (registration)");
            }

            if (action == 1) {
                u = login();
            } else if (action == 2) {
                //With the registration only users with type = CUST can be created
                u = register("CUST");
            } else {
                authType = null;
                outVideo.println("You can insert only 1 (login) or 2 (registration)");
            }
        }

        return u;
    }

    public User login() {

        String username = null;
        String password = null;

        User u = null;

        while (u == null) {
            outVideo.println("Username:");
            try {
                username = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outVideo.println("Password:");
            try {
                password = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            u = UserDAO.findUserByUsername(username);

            if (u == null || !password.equals(u.getPassword())) {
                outVideo.println("Wrong credentials, try again!");
            }

        }

        return u;
    }

    private User register(String type) {
        String username = null;
        String password = null;

        boolean unique = false;
        while (!unique) {
            outVideo.println("Insert username:");
            try {
                username = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (UserDAO.validUser(username)) {
                unique = true;
            }
        }

        outVideo.println("Insert a password:");
        try {
            password = inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        User u = new User(username, password, type);
        UserDAO.addUser(u);

        return u;
    }

    private void searchUsers() {
        ArrayList<String> usersIds = UserDAO.findAllUsers();

        Iterator<String> iterator = usersIds.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            i = i++;
            String s = iterator.next();
            outVideo.println(i + " " + s);
        }

        int n = -1;
        while (n == -1) {
            outVideo.println("Which element do you want to select?");
            String choice = " ";
            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                n = Integer.parseInt(choice);

                if (n > i || n < 1) {
                    n = -1;
                    outVideo.println("Insert a number between 1 and " + i);
                }
            } catch (NumberFormatException nfe) {
                n = -1;
                outVideo.println("You must insert a number");
            }
        }

        User u = UserDAO.findUserByUsername(usersIds.get(n - 1));

        outVideo.println(u);

        manageUser(u);
    }

    private void manageUser(User u) {
        outVideo.println("Do you want to delete it? (y/n)");
        boolean del = false;
        String d = null;
        while (d == null) {
            try {
                d = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (d.equalsIgnoreCase("y")) {
                del = true;
            } else if (!d.equalsIgnoreCase("n")) {
                d = null;
                outVideo.println("You can answer only y or n");
            }
        }

        if (del) {
            UserDAO.deleteUser(u);
        }
    }

    private void addProduct() {
        outVideo.println("Insert name:");
        String name = null;
        try {
            name = inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outVideo.println("Insert brand:");
        String brand = null;
        try {
            brand = inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outVideo.println("Insert main category:");
        String mainCategory = null;
        try {
            mainCategory = inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> categories = new ArrayList<>();

        String cat = null;
        outVideo.println("Insert the other categories: (when you have finished insert x)");
        while (cat == null) {
            try {
                cat = inKeyboard.readLine();
                if (!cat.equalsIgnoreCase("x")) {
                    categories.add(cat);
                    cat = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int n = -1;
        outVideo.println("How many items are available?");
        while (n == -1) {
            String number = " ";
            try {
                number = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                n = Integer.parseInt(number);

                if (n < 0) {
                    n = -1;
                    outVideo.println("Insert a non-negative number");
                }
            } catch (NumberFormatException nfe) {
                n = -1;
                outVideo.println("You must insert a number");
            }

            Double price = null;
            while (price == null) {
                outVideo.println("Insert the price:");
                try {
                    price = Double.parseDouble(inKeyboard.readLine());
                    if (price < 0) {
                        price = null;
                        outVideo.println("Products cannot be free!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException nfe) {
                    price = null;
                    outVideo.println("You must insert a price number");
                }
            }

            outVideo.println("Insert the description:");
            String description = null;
            try {
                description = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Double rate = null;
            while (rate == null) {
                outVideo.println("Insert a rate (from 1.0 to 5.0)");
                try {
                    rate = Double.parseDouble(inKeyboard.readLine());
                    if (rate < 1.0 || rate > 5.0) {
                        rate = null;
                        outVideo.println("You must insert a rate between 1.0 and 5.0");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException nfe) {
                    rate = null;
                    outVideo.println("You must insert a rate between 1.0 and 5.0");
                }
            }

            Product product = new Product(name, brand, mainCategory, categories, n, price, description, null, rate);
            ProductDAO.insertProduct(product);
            outVideo.println("DONE.");
        }
    }

    private void searchProducts(User user) {

        outVideo.println("What do you want to search?");

        String research = "";
        try {
            research = inKeyboard.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        HashMap<String, String> results = new HashMap<>();

        results = ProductDAO.findProductsByString(research);

        //Shows all the results of the search
        int limit = showResults(results);

        if (limit != 0) {
            //Retrieves the ID of the selected product
            String id = chooseElement(results, limit);

            //Retrieves the details of the selected product
            Product p = ProductDAO.findProductById(id);

            //Prints the details of the selected product
            outVideo.println(p);

            manageProduct(p, user);
        }
    }

    private int showResults(HashMap<String, String> results) {

        if (results.isEmpty()) {
            outVideo.println("No results found!");
            return 0;
        } else {
            outVideo.println("Results found: ");
            int i = 0;
            for (String value : results.values()) {
                i++;
                System.out.println(i + " --> " + value);
            }
            return i;
        }
    }

    private String chooseElement(HashMap<String, String> results, int limit) {
        int n = -1;
        while (n == -1) {
            outVideo.println("Which element do you want to select?");
            String choice = " ";
            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                n = Integer.parseInt(choice);

                if (n > limit || n < 1) {
                    n = -1;
                    outVideo.println("Insert a number between 1 and " + limit);
                }
            } catch (NumberFormatException nfe) {
                n = -1;
                outVideo.println("You must insert a number");
            }
        }

        int i = 0;
        String k = null;
        for (String key : results.keySet()) {
            i++;
            if (i == n) {
                k = key;
                break;
            }
        }
        return k;
    }

    private void manageProduct(Product p, User u) {
        outVideo.println("What do you want to do now?");
        outVideo.println("a --> Add the product to my cart");
        outVideo.println("r --> Add a review for the product");

        if (u.getType().equals("EMP")) {
            outVideo.println("m --> Modify the quantity of the product");
            outVideo.println("d --> Delete the product");
        }

        outVideo.println("b --> Go back to the main menu");

        String choice = null;
        while (choice == null) {
            try {
                choice = inKeyboard.readLine();
                if (choice.equals("a")) {
                    ProductDAO.insertProductToCart(p, u);
                } else if (choice.equals("r")) {
                    addReview(p, u);
                } else if (u.getType().equals("EMP") && choice.equals("m")) {
                    modifyQuantity(p);
                } else if (u.getType().equals("EMP") && choice.equals("d")) {
                    ProductDAO.deleteProduct(p);
                } else if (!choice.equals("b")) {
                    outVideo.println("WRONG INPUT");
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e);
                e.printStackTrace();
            }
        }
    }

    private void addReview(Product p, User u) {
        outVideo.println("Insert title:");
        String title = null;
        try {
            title = inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outVideo.println("Insert text:");
        String text = null;
        try {
            text = inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Double rate = null;
        while (rate == null) {
            outVideo.println("Insert a rate (from 1.0 to 5.0)");
            try {
                rate = Double.parseDouble(inKeyboard.readLine());
                if (rate < 1.0 || rate > 5.0) {
                    rate = null;
                    outVideo.println("You must insert a rate between 1.0 and 5.0");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException nfe) {
                rate = null;
                outVideo.println("You must insert a rate between 1.0 and 5.0");
            }
        }

        outVideo.println("Do you recommend it? (y/n)");
        boolean recommend = false;
        String r = null;
        while (r == null) {
            try {
                r = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (r.equalsIgnoreCase("y")) {
                recommend = true;
            } else if (!r.equalsIgnoreCase("n")) {
                r = null;
                outVideo.println("You can answer only y or n");
            }
        }

        Review review = new Review(title, text, rate, recommend, p.getId(), u.getUsername());
        ReviewDAO.insertReview(review);
        outVideo.println("DONE.");
    }

    private void modifyQuantity(Product p) {
        outVideo.println("Actually are available " + p.getAvailableItems() + "items.");
        int n = -1;
        while (n == -1) {
            outVideo.println("Insert the new quantity:");
            String number = null;
            try {
                number = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                n = Integer.parseInt(number);

                if (n < 0) {
                    n = -1;
                    outVideo.println("Insert a non-negative number");
                }
            } catch (NumberFormatException nfe) {
                n = -1;
                outVideo.println("You must insert a number");
            }

            ProductDAO.updateProductQuantity(p, n);
        }
    }

    private void searchOrders(User u) {
        HashMap<String, String> ordersList = OrderDAO.findOrdersByUserId(u.getUsername());

        //Shows all the orders
        int limit = showResults(ordersList);

        if (limit != 0) {
            //Retrieves the ID of the selected product
            String id = chooseElement(ordersList, limit);

            //Retrieves the details of the selected product
            Order o = OrderDAO.findOrderById(id);

            //Prints the details of the selected product
            outVideo.println(o);

            manageOrder(o, u);
        }
    }

    private void manageOrder(Order o, User u) {
        outVideo.println("What do you want to do now?");
        if (u.getType().equals("EMP")) {
            outVideo.println("u --> Update the state of the order");
        }
        if (o.getState().equals("OPENED")) {
            outVideo.println("d --> Delete the order");
        }
        outVideo.println("b --> Go back to the main menu");
        String choice = null;
        while (choice == null) {
            try {
                choice = inKeyboard.readLine();

                if (choice.equals("d")) {
                    OrderDAO.deleteOrder(o);
                } else if (u.getType().equals("EMP") && choice.equals("u")) {
                    modifyState(o);
                } else if (!choice.equals("b")) {
                    choice = null;
                    outVideo.println("WRONG INPUT");
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e);
                e.printStackTrace();
            }
        }
    }

    private void modifyState(Order o) {
        outVideo.println("The actual state of the order is: " + o.getState());
        outVideo.println("Choose the new state:");
        outVideo.println("o -> OPENED");
        outVideo.println("t -> IN TRANSIT");
        outVideo.println("c -> CLOSED");

        String choice = null;
        while (choice == null) {
            try {
                choice = inKeyboard.readLine();

                if (choice.equals("o")) {
                    OrderDAO.updateOrderState(o, "OPENED");
                } else if (choice.equals("t")) {
                    OrderDAO.updateOrderState(o, "IN TRANSIT");
                } else if (!choice.equals("c")) {
                    OrderDAO.updateOrderState(o, "CLOSED");
                } else {
                    choice = null;
                    outVideo.println("WRONG INPUT");
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e);
                e.printStackTrace();
            }
        }
    }

    private void retrieveCart(User u) {
        HashMap<String, String> productsList = ProductDAO.findCartProductsByUser(u);

        //Shows all the products
        outVideo.println("Your cart products: ");
        int limit = showResults(productsList);

        String choice = null;
        while (choice == null) {
            outVideo.println("What do you want to do now?");
            if (limit != 0) {
                outVideo.println("s --> See the details of a product");
            }
            outVideo.println("p --> Purchase the cart");
            outVideo.println("b --> Go back to the main menu");
            try {
                choice = inKeyboard.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (choice.equals("s") && limit != 0) {
                manageCart(limit, productsList, u);
                choice = null;
            } else if (choice.equals("p")) {
                purchaseCart(productsList, u);
            } else if (!choice.equals("b")) {
                outVideo.println("WRONG INPUT");
            }
        }

    }

    private void purchaseCart(HashMap<String, String> productsIds, User user) {

        ArrayList<Product> productsList = new ArrayList<>();
        Double totalAmount = 0.0;
        for (String key : productsIds.keySet()) {

            Product p = ProductDAO.findProductById(key);
            productsList.add(p);

            //The purchased products are removed from the cart
            ProductDAO.removeFromCart(key, user);

            //Compute the total price for the order
            totalAmount = totalAmount + p.getPrice();
        }

        Order order = new Order(productsList, totalAmount, user.getUsername());
        OrderDAO.insertOrder(order);
    }


    private void manageCart(int limit, HashMap<String, String> productsList, User user) {
        if (limit != 0) {
            //Retrieves the ID of the selected product
            String id = chooseElement(productsList, limit);
            //Retrieves the details of the selected product
            Product p = ProductDAO.findProductById(id);
            //Prints the details of the selected product
            outVideo.println(p);

            outVideo.println("Do you want to remove the product from the cart? (y/n)");
            boolean answer = false;
            String a = null;
            while (a == null) {
                try {
                    a = inKeyboard.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (a.equalsIgnoreCase("y")) {
                    answer = true;
                } else if (!a.equalsIgnoreCase("n")) {
                    a = null;
                    outVideo.println("You can answer only y or n");
                }
            }
            if (answer) {
                ProductDAO.removeFromCart(id, user);
            }
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
    }
}