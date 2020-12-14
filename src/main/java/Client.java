import java.io.*;
import java.util.HashMap;

public class Client {
    private BufferedReader inKeyboard;
    private PrintWriter outVideo;

    //connection with the database???
    //private static Database database = new Database();

    public Client() {

        prepareIO();

        while (true) {
            outVideo.println("Welcome " + "user"); //sostituire con nome utente
            outVideo.println("What do you want to do?");
            outVideo.println("s --> Search products");
            outVideo.println("o --> Show my orders");
            outVideo.println("c --> Go to cart");
            outVideo.println("q --> Quit");
            outVideo.println();
            outVideo.println();
            outVideo.println("--- Suggested products ---");
            outVideo.println(); //inserire la tabella con i prodotti e l'indice x selezionarli


            try {
                String choice = inKeyboard.readLine();

                if (choice.equals("s"))
                    searchProducts();
                else if (choice.equals("o"))
                    books();
                else if (choice.equals("c"))
                    publishers();
                else if (choice.equals("q")) {
                    outVideo.println("Goodbye!");
                    //database.closeDB();   -> CLOSE CONNNECTION
                    //anche le funzioni x andare al singolo prodotto selezionato
                    break;
                } else
                    outVideo.println("WRONG INPUT");
            } catch (Exception e) {
                System.out.println("Exception: " + e);
                e.printStackTrace();
            }
        }
    }

    private void prepareIO() {

        inKeyboard = new BufferedReader(new InputStreamReader(System.in));
        outVideo = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);

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
            outVideo.println("Which product do you want to select?");
            String choice = " ";
            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                n = Integer.parseInt(choice);

                if (n > limit || n < 1) {
                    outVideo.println("Insert a number between 1 and " + limit);
                }
            } catch (NumberFormatException nfe) {
                n = -1;
                outVideo.println("You must insert a number");
            }
        }

        int i = 0;
        for (String key : results.keySet()) {
            i++;
            if (i == n) {
                return key;
            }
        }

        return null;
    }


    private void searchProducts() {

        outVideo.println("What do you want to search? ");

        String research = "";
        try {
            research = inKeyboard.readLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        HashMap<String, String> results = null;

        // results = metodo che cerca la parola nel DB

        //Shows all the results of the search
        int limit = showResults(results);

        //Retrieve the ID of the selected product
        String id = chooseElement(results, limit);

        //Product p = new Product();
        // p = metodo che recupera i dettagli del prodotto scelto;

        //showDetails(p);

    }

    private void authors() {

        while (true) {
            outVideo.println("What do you want to do?");
            outVideo.println("1. Add a new author");
            outVideo.println("2. Delete an author");
            outVideo.println("3. Read list of authors");
            outVideo.println("4. Go back");

            String choice = "";

            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            if (choice.equals("1")) {
                addAuthor();
                break;
            } else if (choice.equals("2")) {
                deleteAuthor();
                break;
            } else if (choice.equals("3")) {
                showAuthorsList();
            } else if (choice.equals("4"))
                break;
            else
                outVideo.println("WRONG INPUT");
        }
    }

    private void addAuthor() {
        try {
            outVideo.println("Firstname:");
            String firstname = inKeyboard.readLine();

            outVideo.println("Lastname:");
            String lastname = inKeyboard.readLine();

            outVideo.println("Biography:");
            String biography = inKeyboard.readLine();

            AuthorKV.insertAuthor(database, firstname, lastname, biography);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    private void deleteAuthor() {

        int id = -1;
        showAuthorsList();
        while (id == -1) {
            outVideo.println("Which author do you want to delete?");
            String choice = " ";
            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                id = Integer.parseInt(choice);
            } catch (NumberFormatException nfe) {
                id = -1;
                outVideo.println("The ID must be a number");
            }
        }

        AuthorKV.deleteAuthor(database, id);
    }

    private void showAuthorsList() {
        List<AuthorBean> list = AuthorKV.getAuthorsList(database);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%10s %20s %20s %20s", "AUTHOR ID", "FIRST NAME", "LAST NAME", "BIOGRAPHY");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        Iterator<AuthorBean> it = list.iterator();
        while (it.hasNext()) {
            AuthorBean a = (AuthorBean) it.next();
            System.out.format("%10d %20s %20s %20s", a.getId(), a.getFirstname(), a.getLastname(), a.getBiography());
            System.out.println();
        }

        System.out.println("-----------------------------------------------------------------------------");

    }

    private void books() {

        while (true) {
            outVideo.println("What do you want to do?");
            outVideo.println("1. Add a new book");
            outVideo.println("2. Delete a book");
            outVideo.println("3. Modify the quantity of a book");
            outVideo.println("4. Read list of books");
            outVideo.println("5. Go back");

            try {
                String choice = inKeyboard.readLine();

                if (choice.equals("1")) {
                    addBook();
                    break;
                } else if (choice.equals("2")) {
                    deleteBook();
                    break;
                } else if (choice.equals("3")) {
                    updateBook();
                    break;
                } else if (choice.equals("4")) {
                    showBooksList();
                } else if (choice.equals("5"))
                    break;
                else
                    outVideo.println("WRONG INPUT");
            } catch (Exception e) {
                System.out.println("Exception: " + e);
                e.printStackTrace();
            }
        }
    }

    private void addBook() {
        try {
            outVideo.println("Title:");
            String title = inKeyboard.readLine();
            BookKV.getBooksList(database);

            AuthorKV.getAuthorsList(database);
            outVideo.println("Author ID:");
            int authorID = Integer.parseInt(inKeyboard.readLine());

            outVideo.println("Price:");
            float price = Float.parseFloat(inKeyboard.readLine());

            outVideo.println("Category:");
            String category = inKeyboard.readLine();

            outVideo.println("Publication year:");
            int pubYear = Integer.parseInt(inKeyboard.readLine());

            outVideo.println("Number of pages:");
            int numOfPages = Integer.parseInt(inKeyboard.readLine());

            PublisherKV.getPublishersList(database);
            outVideo.println("Publisher ID:");
            int publisher = Integer.parseInt(inKeyboard.readLine());

            outVideo.println("Quantity:");
            int quantity = Integer.parseInt(inKeyboard.readLine());

            BookKV.insertBook(database, title, price, category, pubYear, numOfPages, authorID, publisher, quantity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {

        int id = -1;
        showBooksList();
        while (id == -1) {
            outVideo.println("Which book do you want to delete?");
            String choice = " ";
            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                id = Integer.parseInt(choice);
            } catch (NumberFormatException nfe) {
                id = -1;
                outVideo.println("The ID must be a number");
            }
        }

        BookKV.deleteBook(database, id);
    }

    private void updateBook() {
        try {
            showBooksList();
            int id = -1;

            while (id == -1) {
                outVideo.println("Which book do you want to update?");
                String choice = " ";
                try {
                    choice = inKeyboard.readLine();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                try {
                    id = Integer.parseInt(choice);
                } catch (NumberFormatException nfe) {
                    id = -1;
                    outVideo.println("The ID must be a number");
                }
            }

            outVideo.println("What do you want to do? (i --> increase d --> decrease n --> insert a new value)");

            String choice = inKeyboard.readLine();

            if (choice.equals("i"))
                BookKV.increaseQuantity(database, id);
            else if (choice.equals("d"))
                BookKV.decreaseQuantity(database, id);
            else if (choice.equals("n")) {

                int v = -1;

                while (v == -1) {
                    outVideo.println("What is the new value?");
                    String c = " ";
                    try {
                        c = inKeyboard.readLine();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    try {
                        v = Integer.parseInt(c);
                    } catch (NumberFormatException nfe) {
                        v = -1;
                        outVideo.println("Insert a number");
                    }
                }

                BookKV.updateQuantity(database, id, v);
            } else
                outVideo.println("Only these three operation are allowed.");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    private void showBooksList() {
        List<BookBean> list = BookKV.getBooksList(database);
        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("\t%-10s%-45s%-25s%-15s%-15s%-20s%-15s%s", "BOOK ID", "TITLE", "AUTHOR", "CATEGORY",
                "PUBL YEAR", "PUBLISHER", "QUANTITY", "PRICE    ");
        System.out.println();
        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------------------------------------");
        Iterator<BookBean> it = list.iterator();
        while (it.hasNext()) {
            BookBean b = (BookBean) it.next();
            System.out.format("\t%-10d%-45s%-25d%-15s%-15d%-20d%-15d%.2f", b.getId(), b.getTitle(), b.getAuthor(),
                    b.getCategory(), b.getPublicationYear(), b.getPublisherId(), b.getQuantity(), b.getPrice());

            System.out.println();
        }

        System.out.println(
                "---------------------------------------------------------------------------------------------------------------------------------------------------------------");

    }

    private void publishers() {

        while (true) {
            outVideo.println("What do you want to do?");
            outVideo.println("1. Add a new publisher");
            outVideo.println("2. Delete a publisher");
            outVideo.println("3. Read list of publisher");
            outVideo.println("4. Go back");

            String choice = "";

            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            if (choice.equals("1")) {
                addPublisher();
                break;
            } else if (choice.equals("2")) {
                deletePublisher();
                break;
            } else if (choice.equals("3")) {
                showPublishersList();
            } else if (choice.equals("4")) {
                break;
            } else
                outVideo.println("WRONG INPUT");
        }

    }

    private void addPublisher() {
        try {
            outVideo.println("Name:");
            String name = inKeyboard.readLine();

            outVideo.println("Location:");
            String location = inKeyboard.readLine();

            PublisherKV.insertPublisher(database, name, location);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    private void deletePublisher() {

        int id = -1;
        showPublishersList();
        while (id == -1) {
            outVideo.println("Which publisher do you want to delete?");
            String choice = " ";
            try {
                choice = inKeyboard.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                id = Integer.parseInt(choice);
            } catch (NumberFormatException nfe) {
                id = -1;
                outVideo.println("The ID must be a number");
            }
        }

        PublisherKV.deletePublisher(database, id);
    }

    private void showPublishersList() {
        List<PublisherBean> list = PublisherKV.getPublishersList(database);

        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%20s %20s %20s", "PUBLISHER ID", "NAME", "LOCATION");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");

        Iterator<PublisherBean> it = list.iterator();
        while (it.hasNext()) {
            PublisherBean p = (PublisherBean) it.next();
            System.out.format("%20d %20s %20s", p.getId(), p.getName(), p.getLocation());
            System.out.println();
        }

        System.out.println("-----------------------------------------------------------------------------");

    }

    public static void main(String[] args) {

        database.openDB();
        database.prepareDatabase();

        Client c = new Client();
    }

}
