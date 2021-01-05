import DAOs.GraphDatabaseDAO;
import beans.*;
import DAOs.DocumentDatabaseDAOs.*;
import DAOs.KVDatabaseDAO;
import com.mongodb.client.MongoDatabase;
import utilities.Types.*;
import utilities.Validation;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static utilities.Validation.takeMandatoryString;
import static utilities.Validation.takePositiveInt;

public class Client {
    private BufferedReader inKeyboard;
    private PrintWriter outVideo;

    private static final KVDatabaseDAO kvDatabase = new KVDatabaseDAO();

    private final ConnectionToMongoDB connection;
    private final MongoDatabase docDatabase;

    public Client() {

        //Setting of the variables for IO operations
        prepareIO();

        outVideo.println("Welcome to E-SHOP!");

        //Open connection to MongoDB
        connection = new ConnectionToMongoDB();
        connection.openConnection("mongodb://172.16.3.145:27020,172.16.3.146:27020,172.16.3.102:27020/" + "retryWrites=true&w=2&wtimeout=10000&readPreference=primary");

        //Locally: "mongodb://localhost:27017"

        docDatabase = connection.getMongoDatabase();

        //Real execution of the application
        execute();
    }

    private void prepareIO() {
        inKeyboard = new BufferedReader(new InputStreamReader(System.in));
        outVideo = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    private void execute() {

        User user = null;

        //Main menu
        while (true) {
            if (user != null) {
                outVideo.println();
                outVideo.println("What do you want to do?");
                if (user.getType() == UserType.ADMIN) {
                    outVideo.println("e --> Add a new employee");
                    outVideo.println("u --> Search users");
                    outVideo.println("p --> See statistics about the platform");
                }
                if (user.getType() == UserType.EMPLOYEE || user.getType() == UserType.ADMIN) {
                    outVideo.println("a --> Add a new product");
                    outVideo.println("o --> Search orders");
                }

                outVideo.println("s --> Search products");

                if (user.getType() == UserType.CUSTOMER) {
                    outVideo.println("d --> See details about a suggested product");
                    outVideo.println("c --> Go to cart");
                    outVideo.println("o --> Show my orders");
                }
                outVideo.println("l --> Log out");
                outVideo.println("q --> Quit");
                outVideo.println();
                outVideo.println();

                //List of suggested products
                int limit = 0;
                HashMap<String, String> suggestedProducts = new HashMap<>();

                if (user.getType() == UserType.CUSTOMER) {

                    //Computation of the list of suggested products
                    GraphDatabaseDAO g = new GraphDatabaseDAO();
                    suggestedProducts = g.returnRecommended(user.getUsername());

                    try {
                        g.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    outVideo.println("The users that bought the same product as you, purchased also the products you can see below.");
                    outVideo.println("--- Suggested products ---");

                    //View of all the results of the search
                    limit = showResults(suggestedProducts);
                }

                try {
                    String choice = inKeyboard.readLine();

                    switch (choice) {
                        case "e":
                            if (user.getType() == UserType.ADMIN) {
                                //The administrator can create only profiles for employees
                                register(UserType.EMPLOYEE);
                            } else {
                                outVideo.println("WRONG INPUT");
                            }
                            break;
                        case "p":
                            if (user.getType() == UserType.ADMIN) {
                                seeStatistics();
                            } else {
                                outVideo.println("WRONG INPUT");
                            }
                            break;
                        case "u":
                            if (user.getType() == UserType.ADMIN) {
                                searchUsers();
                            } else {
                                outVideo.println("WRONG INPUT");
                            }
                            break;
                        case "a":
                            if (user.getType() == UserType.ADMIN || user.getType() == UserType.EMPLOYEE) {
                                addProduct();
                            } else {
                                outVideo.println("WRONG INPUT");
                            }
                            break;
                        case "d":
                            if (user.getType() == UserType.CUSTOMER && limit != 0) {
                                //Index of the selected product
                                int index = chooseElement(limit);

                                if (index != 0) {
                                    //Id of the product
                                    String id = retrieveIdFromIndex(suggestedProducts, index);

                                    //Details of the selected product
                                    Product p = ProductDAO.findProductById(docDatabase, id);

                                    //Prints the details of the selected product
                                    outVideo.println(p);

                                    manageProduct(p, user);
                                }
                            } else {
                                outVideo.println("WRONG INPUT");
                            }
                            break;
                        case "c":
                            if (user.getType() == UserType.CUSTOMER) {
                                retrieveCart(user);
                            } else {
                                outVideo.println("WRONG INPUT");
                            }
                            break;
                        case "o":
                            searchOrders(user);
                            break;
                        case "s":
                            searchProducts(user);
                            break;
                        case "l":
                            user = null;
                            //Close connection to KV docDatabase
                            kvDatabase.closeDB();
                            break;
                        case "q":
                            outVideo.println("Goodbye!");

                            //Close connection to KV database
                            kvDatabase.closeDB();

                            //Close connection to document database
                            connection.closeConnection();

                            return;

                        default:
                            outVideo.println("WRONG INPUT");
                    }
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                    e.printStackTrace();
                }
            } else {
                user = authenticate();
                outVideo.println("Hello, " + user.getUsername());
                if (user.getType() != UserType.EMPLOYEE) {
                    kvDatabase.openDB();
                }
            }
        }
    }

    private void seeStatistics() {
        GraphDatabaseDAO g = new GraphDatabaseDAO();
        ArrayList<String> bestCategories = g.returnBestCategoryForMonth();
        ArrayList<String> meanSold = g.returnMeanForMonth();

        outVideo.println("Best categories per  month: ");

        for (String bestCategory : bestCategories) {
            outVideo.println(bestCategory);
        }
        outVideo.println();
        outVideo.println("Most sold categories per year");

        for (String s : meanSold) {
            outVideo.println(s);
        }

        try {
            g.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private User authenticate() {
        outVideo.println("Have you already been here? --> insert 1 to login");
        outVideo.println("Are you new? --> insert 2 to register");

        User u = null;

        String authType = null;
        while (authType == null) {
            try {
                authType = inKeyboard.readLine();

                int action;
                try {
                    action = Integer.parseInt(authType);
                    if (action == 1) {
                        u = login();
                    } else if (action == 2) {
                        //With the registration only CUSTOMER profiles can be created
                        u = register(UserType.CUSTOMER);
                    } else {
                        authType = null;
                        outVideo.println("You can insert only 1 (login) or 2 (registration)");
                    }
                } catch (NumberFormatException nfe) {
                    authType = null;
                    outVideo.println("You can insert only 1 (login) or 2 (registration)");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return u;
    }

    public User login() {

        User u = null;
        boolean logged = false;

        while (!logged) {
            outVideo.println("Username:");
            try {
                String username = inKeyboard.readLine();

                outVideo.println("Password:");

                String password = inKeyboard.readLine();

                u = UserDAO.findUserByUsername(docDatabase, username);

                if (u != null && password.equals(u.getPassword())) {
                    logged = true;
                } else {
                    outVideo.println("Wrong credentials, try again!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return u;
    }

    private User register(UserType type) {

        String username = null;
        String password = null;

        //Insert a new username
        boolean unique = false;
        while (!unique) {
            outVideo.println("Insert username:");
            try {
                username = inKeyboard.readLine();

                if (UserDAO.validUser(docDatabase, username)) {
                    unique = true;
                } else {
                    outVideo.println("Username already in use");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Insert a valid password
        boolean valid = false;
        while (!valid) {
            outVideo.println("Insert a password with at least 8 alphanumeric characters:");
            try {
                password = inKeyboard.readLine();

                if (Validation.validatePassword(password, 8)) {
                    valid = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        User u = new User(username, password, type);
        UserDAO.addUser(docDatabase, u);

        return u;
    }

    private void searchUsers() {

        while (true) {
            ArrayList<String> usersIds = UserDAO.findAllUsernames(docDatabase);

            //Show all the usernames available
            Iterator<String> iterator = usersIds.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                i++;
                String s = iterator.next();
                outVideo.println(i + " --> " + s);
            }

            outVideo.println("0 --> Go back");

            //Choose a user
            int index = chooseElement(i);

            if (index != 0) {
                //Retrieve the user's details
                User u = UserDAO.findUserByUsername(docDatabase, usersIds.get(index - 1));

                outVideo.println(u);

                manageUser(u.getUsername());
            } else {
                break;
            }
        }
    }

    private void manageUser(String username) {
        outVideo.println("Do you want to delete it? (y/n)");
        String d = null;
        while (d == null) {
            try {
                d = inKeyboard.readLine();

                if (d.equalsIgnoreCase("y")) {
                    UserDAO.deleteUser(docDatabase, username);
                    kvDatabase.removeUserFromCart(username);
                    ReviewDAO.deleteReviewsByUsername(docDatabase, username);
                    GraphDatabaseDAO g = new GraphDatabaseDAO();
                    g.deletePerson(username);
                    try {
                        g.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!d.equalsIgnoreCase("n")) {
                    d = null;
                    outVideo.println("You can answer only y or n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addProduct() {
        String id = null;
        while (id == null) {
            outVideo.println("Insert product code: ");
            id = takeMandatoryString(inKeyboard, outVideo);
            Product p = ProductDAO.findProductById(docDatabase, id);
            if (p != null) {
                id = null;
                outVideo.println("This product already exists");
            }
        }

        outVideo.println("Insert name:");
        String name = takeMandatoryString(inKeyboard, outVideo);
        outVideo.println("Insert brand:");
        String brand = takeMandatoryString(inKeyboard, outVideo);
        outVideo.println("Insert main category:");
        String mainCategory = takeMandatoryString(inKeyboard, outVideo);

        ArrayList<String> categories = new ArrayList<>();
        String cat = null;
        outVideo.println("Insert the other categories: (insert x to finish)");
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

        outVideo.println("Insert the price:");
        double price = Validation.takeValidPrice(inKeyboard, outVideo);


        outVideo.println("Insert the description: (press enter to skip this field)");
        String description = null;
        try {
            description = inKeyboard.readLine();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        Product product = new Product(id, name, brand, mainCategory, categories, price, description, null);
        ProductDAO.insertProduct(docDatabase, product);
    }

    private void searchProducts(User user) {

        outVideo.println("What do you want to search?");

        try {
            String research = inKeyboard.readLine();

            while (true) {

                HashMap<String, String> results = ProductDAO.findProductsByString(docDatabase, research);

                //Shows all the results of the search
                int limit = showResults(results);

                outVideo.println("0 --> Go back");

                if (limit != 0) {
                    //Index of the selected product
                    int index = chooseElement(limit);

                    if (index != 0) {

                        //Id of the selected product
                        String id = retrieveIdFromIndex(results, index);

                        //Details of the selected product
                        Product p = ProductDAO.findProductById(docDatabase, id);

                        //Prints the details of the selected product
                        outVideo.println(p);

                        manageProduct(p, user);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private int showResults(HashMap<String, String> results) {

        if (results.isEmpty()) {
            outVideo.println("No results found!");
            return 0;
        } else {
            int i = 0;
            for (String value : results.values()) {
                i++;
                System.out.println(i + " --> " + value);
            }
            return i;
        }
    }

    private int chooseElement(int limit) {
        int n = -1;
        while (n == -1) {
            outVideo.println("Which element do you want to select? (insert 0 to go back)");
            try {
                String choice = inKeyboard.readLine();
                n = Integer.parseInt(choice);

                if (n > limit || n < 0) {
                    n = -1;
                    outVideo.println("Insert a number between 1 and " + limit + "(or 0 to go back)");
                }
            } catch (NumberFormatException nfe) {
                outVideo.println("You must insert a number");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return n;
    }

    private String retrieveIdFromIndex(HashMap<String, String> results, int index) {

        int i = 0;
        String k = null;
        for (String key : results.keySet()) {
            i++;
            if (i == index) {
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

        if (u.getType() == UserType.EMPLOYEE) {
            outVideo.println("m --> Modify the quantity of the product");
            outVideo.println("d --> Delete the product");
        }

        outVideo.println("b --> Go back");

        String choice = null;
        while (choice == null) {
            try {
                choice = inKeyboard.readLine();
                if (choice.equals("a")) {
                    outVideo.println("How many items do you want to add?");
                    int quantity = Validation.takePositiveInt(inKeyboard, outVideo);
                    kvDatabase.insertProductToCart(p, u.getUsername(), quantity);
                } else if (choice.equals("r")) {
                    addReview(p, u);
                } else if (u.getType() == UserType.EMPLOYEE && choice.equals("d")) {
                    ProductDAO.deleteProduct(docDatabase, p.getId());
                    GraphDatabaseDAO g = new GraphDatabaseDAO();
                    g.deleteProduct(p.getId());
                    try {
                        g.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        String title = takeMandatoryString(inKeyboard, outVideo);

        outVideo.println("Insert text:");
        String text = takeMandatoryString(inKeyboard, outVideo);

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
                outVideo.println("You must insert a rate between 1.0 and 5.0");
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        rate = Double.valueOf(decimalFormat.format(rate));

        outVideo.println("Do you recommend it? (y/n)");
        boolean recommend = false;
        String r = null;
        while (r == null) {
            try {
                r = inKeyboard.readLine();

                if (r.equalsIgnoreCase("y")) {
                    recommend = true;
                } else if (!r.equalsIgnoreCase("n")) {
                    r = null;
                    outVideo.println("You can answer only y or n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Review review = new Review(title, text, rate, recommend, p.getId(), u.getUsername());
        ReviewDAO.addReview(docDatabase, review);
    }

    private void searchOrders(User u) {

        HashMap<String, String> ordersList;

        User emp = null;

        if (!(u.getType() == UserType.CUSTOMER)) {
            emp = u;
            u = null;
            while (u == null) {
                outVideo.println("Insert the id of the user that made the order:");
                try {
                    String username = inKeyboard.readLine();

                    u = UserDAO.findUserByUsername(docDatabase, username);

                    if (u == null) {
                        outVideo.println("This user doesn't exist");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        while (true) {
            ordersList = OrderDAO.findOrdersByUsername(docDatabase, u.getUsername());

            if (emp != null) {
                u = emp;
            }

            //Shows all the orders
            int limit = showResults(ordersList);

            if (limit != 0) {
                //Index of the selected product
                int index = chooseElement(limit);

                if (index != 0) {
                    //Id of the selected product
                    String id = retrieveIdFromIndex(ordersList, index);

                    //Retrieves the details of the selected product
                    Order o = OrderDAO.findOrderById(docDatabase, id);

                    //Prints the details of the selected product
                    outVideo.println(o);

                    if (!o.getProducts().isEmpty()) {
                        outVideo.println("Products: \n");

                        HashMap<String, Integer> products = o.getProducts();
                        for (String key : products.keySet()) {
                            Product p = ProductDAO.findProductById(docDatabase, key);
                            outVideo.println(p.getName() + "\n");
                        }
                    }
                    manageOrder(o, u);
                }
            }

            break;
        }

    }

    private void manageOrder(Order o, User u) {
        outVideo.println("What do you want to do now?");
        if (u.getType() == UserType.EMPLOYEE) {
            outVideo.println("u --> Update the state of the order");
        }
        if (o.getState() == OrderState.OPENED) {
            outVideo.println("d --> Delete the order");
        }
        outVideo.println("b --> Go back");
        String choice = null;
        while (choice == null) {
            try {
                choice = inKeyboard.readLine();

                if (choice.equals("d")) {
                    OrderDAO.deleteOrder(docDatabase, o.getId());
                    //We have to remove the PURCHASE relation in Neo4J!!!!
                } else if (u.getType() == UserType.EMPLOYEE && choice.equals("u")) {
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
        outVideo.println("t -> IN_TRANSIT");
        outVideo.println("c -> CLOSED");

        String choice = null;
        while (choice == null) {
            try {
                choice = inKeyboard.readLine();

                if (choice.equals("o")) {
                    OrderDAO.updateOrderState(docDatabase, o.getId(), OrderState.OPENED.toString());
                } else if (choice.equals("t")) {
                    OrderDAO.updateOrderState(docDatabase, o.getId(), OrderState.IN_TRANSIT.toString());
                } else if (!choice.equals("c")) {
                    OrderDAO.updateOrderState(docDatabase, o.getId(), OrderState.CLOSED.toString());
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
        HashMap<String, String> productsList = kvDatabase.getProductsByUsername(u.getUsername());

        //Shows all the products
        outVideo.println("Your cart products: ");
        int limit = showResults(productsList);

        if (limit != 0) {
            double totalAmount = 0.0;
            for (String value : productsList.values()) {
                String[] split = value.split(" --> ");
                String[] split2 = split[1].split(" x ");
                totalAmount = totalAmount + Double.parseDouble(split2[0]) * Integer.parseInt(split2[1]);
            }
            outVideo.println("Total Amount: " + Math.round(totalAmount * 100.0) / 100.0 + "\n");

            String choice = "";
            while (choice.equals("")) {
                outVideo.println("What do you want to do now?");
                outVideo.println("s --> See the details of a product");
                outVideo.println("q --> Modify the quantity for a product");
                outVideo.println("a --> Buy all");
                outVideo.println("b --> Go back");
                try {
                    choice = inKeyboard.readLine();

                    if (choice.equals("s")) {
                        manageCart(limit, productsList, u);

                    } else if (choice.equals("a")) {
                        purchaseCart(productsList, u);
                    } else if (choice.equals("q")) {
                        modifyQuantity(limit, productsList, u);
                    } else if (!choice.equals("b")) {
                        outVideo.println("WRONG INPUT");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void purchaseCart(HashMap<String, String> productsIds, User user) {

        HashMap<String, Integer> products = new HashMap<>();
        double totalAmount = 0.00;

        for (String key : productsIds.keySet()) {

            String value = productsIds.get(key);
            String[] split = value.split(" --> ");
            String[] split2 = split[1].split(" x ");

            products.put(key, Integer.parseInt(split2[1]));

            totalAmount = totalAmount + Double.parseDouble(split2[0]) * Integer.parseInt(split2[1]);
        }

        Order order = new Order(products, Math.round(totalAmount * 100.0) / 100.0, user.getUsername(), OrderState.OPENED);
        OrderDAO.insertOrder(docDatabase, order);

        GraphDatabaseDAO g = new GraphDatabaseDAO();
        g.insertPerson(user.getUsername());
        for (String key : order.getProducts().keySet()) {
            g.insertProduct(ProductDAO.findProductById(docDatabase, key).getName(), ProductDAO.findProductById(docDatabase, key).getMainCategory(), key);
            g.insertRelationship(user.getUsername(), ProductDAO.findProductById(docDatabase, key).getName());
        }


        try {
            g.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //The purchased products are removed from the cart
        kvDatabase.removeUserFromCart(user.getUsername());
    }


    private void manageCart(int limit, HashMap<String, String> productsList, User user) {
        if (limit != 0) {
            //Index of the selected product
            int index = chooseElement(limit);

            if (index != 0) {
                //Index of the selected product
                String id = retrieveIdFromIndex(productsList, index);
                //Details of the selected product
                Product p = ProductDAO.findProductById(docDatabase, id);
                //Prints the details of the selected product
                outVideo.println(p);

                outVideo.println("Do you want to remove the product from the cart? (y/n)");
                boolean answer = false;
                String a = null;
                while (a == null) {
                    try {
                        a = inKeyboard.readLine();

                        if (a.equalsIgnoreCase("y")) {
                            answer = true;
                        } else if (!a.equalsIgnoreCase("n")) {
                            a = null;
                            outVideo.println("You can answer only y or n");
                        }
                        if (answer) {
                            kvDatabase.removeProductFromCart(id, user.getUsername());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void modifyQuantity(int limit, HashMap<String, String> productsList, User user) {
        if (limit != 0) {
            //Index of the selected product
            int index = chooseElement(limit);

            if (index != 0) {
                //Index of the selected product
                String id = retrieveIdFromIndex(productsList, index);

                outVideo.println("How many items do you want to put into your cart?");
                int q = takePositiveInt(inKeyboard, outVideo);
                kvDatabase.updateQuantityOfProduct(id, user.getUsername(), q);
            }
        }
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Client c = new Client();
    }
}