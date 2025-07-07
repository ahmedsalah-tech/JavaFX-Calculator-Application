// Advanced Calculator Project using JavaFX
// Includes basic and scientific operations with a customizable GUI (Dark/Light mode)
// Enables a side panel for advanced operations
package calculator;

// Importing all necessary packages and libraries for building the JavaFX user interface and handling events
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;
import java.util.function.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.StageStyle;

public class calculator extends Application {

    // Field for displaying input and results
    private TextField display = new TextField();
    // Side panel for advanced operations
    private VBox advancedPanel = new VBox(15);
    private boolean advancedVisible = false; // Panel visibility state
    private ScrollPane scrollPane; // For scrolling inside the panel
    private double lastAnswer = 0; // Stores the last result
    private boolean isDarkMode = false; // Theme state

    @Override
    public void start(Stage primaryStage) {

        display.setEditable(false);
        display.setMinHeight(50);
        display.setStyle("-fx-font-size: 18;");

        // Create basic operation buttons
        GridPane basicButtons = createBasicButtons();

        // Button to toggle the side panel
        Button toggleAdvanced = new Button("Advanced Panel");
        toggleAdvanced.getStyleClass().add("toggleAdvanced");
        toggleAdvanced.setOnAction(e -> toggleAdvancedPanel());

        // Setup advanced operations inside the panel
        createAdvancedButtons();

        //Setup the side panel
        advancedPanel.setPadding(new Insets(10));
        advancedPanel.getStyleClass().add("advanced");
        advancedPanel.setTranslateX(300);
        advancedPanel.setPrefWidth(300);
        //advancedPanel = false;

        //Setup scrollPane the side panel
        scrollPane = new ScrollPane(advancedPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(300);
        scrollPane.setMouseTransparent(false);
        scrollPane.setTranslateX(300);
        scrollPane.setVisible(false);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Container for theme toggle button
        StackPane theme = new StackPane();

        // Main layout of the calculator
        VBox calculatorLayout = new VBox(10, display, basicButtons, toggleAdvanced, theme);
        calculatorLayout.setAlignment(Pos.CENTER);
        calculatorLayout.setPadding(new Insets(10));
        StackPane root = new StackPane(calculatorLayout, scrollPane);

        Scene scene = new Scene(root);

        // Hide side panel when clicking outside it
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (advancedVisible && !advancedPanel.localToScene(advancedPanel.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                hideAdvancedPanel();
            }
        });

        // Button to toggle between dark and light mode
        Button toggleTheme = new Button("Toggle Theme >");
        toggleTheme.setStyle("""
                     -fx-font-size: 10;-fx-min-width: 80px;
                     -fx-min-height: 20px;""");

        StackPane.setAlignment(toggleTheme, Pos.TOP_RIGHT);
        theme.getChildren().add(toggleTheme);

        // Button to toggle setOnAction...
        toggleTheme.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            scene.getStylesheets().clear();
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("Dark.css").toExternalForm());

            } else {
                scene.getStylesheets().add(getClass().getResource("light.css").toExternalForm());
            }
        });
        // Load default theme (light)
        scene.getStylesheets().add(getClass().getResource("light.css").toExternalForm());

        //Prevent closing the window with the "✖" button
        primaryStage.setOnCloseRequest(evnt -> {
            evnt.consume();
        });

        scene.setOnKeyPressed(event -> {
            String key = event.getText(); // Get the character that was typed

            switch (event.getCode()) {

                case ENTER -> handleInput("=");           // Press Enter to evaluate
                case BACK_SPACE -> handleInput("⌫");      // Delete last character
                case ESCAPE, DELETE -> handleInput("C");  // Clear the display

                // Handle Shift + 5 to insert %
                case DIGIT5 -> {
                    if (event.isShiftDown()) handleInput("%");
                    else handleInput("5");
                }

                // Handle Shift + 6 to insert ^
                case DIGIT6 -> {
                    if (event.isShiftDown()) handleInput("^");
                    else handleInput("6");
                }

                // Handle Shift + 9 to insert (
                case DIGIT9 -> {
                    if (event.isShiftDown()) handleInput("(");
                    else handleInput("9");
                }

                // Handle Shift + 0 to insert )
                case DIGIT0 -> {
                    if (event.isShiftDown()) handleInput(")");
                    else handleInput("0");
                }

                // Digits 1 to 4 and 7 to 8
                case DIGIT1, NUMPAD1 -> handleInput("1");
                case DIGIT2, NUMPAD2 -> handleInput("2");
                case DIGIT3, NUMPAD3 -> handleInput("3");
                case DIGIT4, NUMPAD4 -> handleInput("4");
                case DIGIT7, NUMPAD7 -> handleInput("7");
                case DIGIT8, NUMPAD8 -> handleInput("8");

                // Additional Numpad digit cases not handled above
                case NUMPAD0 -> handleInput("0");
                case NUMPAD5 -> handleInput("5");
                case NUMPAD6 -> handleInput("6");
                case NUMPAD9 -> handleInput("9");

                // Handle math operators from main keyboard or numpad
                case ADD, PLUS -> handleInput("+");
                case SUBTRACT, MINUS -> handleInput("-");
                case MULTIPLY -> handleInput("*");
                case DIVIDE, SLASH -> handleInput("/");

                default -> {
                    // Allow typing dot, comma, or math operators
                    if (".,*/+-".contains(key)) {
                        handleInput(key);
                    }

                    // Use key 'm' or 'n' to toggle plus/minus
                    if (key.equalsIgnoreCase("m") || key.equalsIgnoreCase("n")) {
                        handleInput("±");
                    }
                }
            }
        });


        scene.getRoot().requestFocus();

        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("JavaFX Calculator");
        primaryStage.setScene(scene);
        primaryStage.setHeight(700);
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("icon.png"))));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Toggle side panel visibility
    private void toggleAdvancedPanel() {
        if (advancedVisible) {
            hideAdvancedPanel();
        } else {
            showAdvancedPanel();
        }
    }

    //Show the side panel
    private void showAdvancedPanel() {
        advancedPanel.setTranslateX(0);
        advancedVisible = true;
        scrollPane.setTranslateX(0);
        scrollPane.setVisible(true);
        display.clear();
    }

    //Hide the side panel
    private void hideAdvancedPanel() {
        scrollPane.setVisible(false);
        advancedVisible = false;
    }

    // Create basic operation buttons (6x5 grid)
    private GridPane createBasicButtons() {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        String[][] buttons = {
            {"Exit", "exp", "log", "Ans", "ln"},
            {"^", "%", "(", ")", "⌫"},
            {"±", "7", "8", "9", "/"},
            {"√", "4", "5", "6", "*"},
            {",", "1", "2", "3", "-"},
            {".", "0", "=", "+", "C"},};

        //Repetition create basic operation buttons (6x5 grid)
        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String symbol = buttons[row][col];
                Button btn = new Button(symbol);
                btn.setMinSize(50, 50);
                btn.setOnAction(e -> {
                    handleInput(symbol);
                    display.getScene().getRoot().requestFocus();

                });
                grid.add(btn, col, row);
            }
        }
        return grid;
    }

    // ================== advanced operation on the side panel (expression.. ) ===================
    // Create advanced operation buttons on the side panel
    private void createAdvancedButtons() {
        advancedPanel.getChildren().clear();

        //Button hide the side panel
        Button closeBtn = new Button("✖");
        closeBtn.getStyleClass().add("closeBtn");
        closeBtn.setOnAction(e -> hideAdvancedPanel());
        HBox topBar = new HBox(closeBtn);
        topBar.setAlignment(Pos.TOP_LEFT);
        advancedPanel.getChildren().add(topBar);

        // Organize advanced functions into categories
        Map<String, List<String>> operations = new LinkedHashMap<>();
        operations.put("Trigonometric", Arrays.asList("sin", "cos", "tan", "asin", "acos", "atan"));
        operations.put("Root Operations", Arrays.asList("sqrt", "cbrt"));
        operations.put("Exponential Power", Arrays.asList("square", "cube"));
        operations.put("Approximation", Arrays.asList("round", "ceil", "floor"));
        operations.put("Statistical", Arrays.asList("avg", "min", "max"));
        operations.put("Validation", Arrays.asList("prime", "palindrome", "armstrong"));
        operations.put("Number Theory", Arrays.asList("GCD", "LCM"));

        // For each entry in the 'operations' map, which holds a category title and a list of operation names
        for (Map.Entry<String, List<String>> entry : operations.entrySet()) {

            // Get the category title (e.g., "Trigonometric")
            String title = entry.getKey();

            // Get the list of operations in this category (e.g., ["sin", "cos", "tan"])
            List<String> ops = entry.getValue();
            // Create a label for the category title and apply a CSS style class "section"
            Label sectionLabel = new Label(title);
            sectionLabel.getStyleClass().add("section");
            // Create a FlowPane to hold the buttons for each operation, with horizontal and vertical gaps
            FlowPane buttonsPane = new FlowPane();
            buttonsPane.setHgap(5);
            buttonsPane.setVgap(5);

            // For each operation name in the current category list
            for (String op : ops) {
                // Create a button with the operation name as text
                Button opButton = new Button(op);
                opButton.setPrefWidth(80);

                // Define the button's action: when clicked, append the operation name followed by "(" to the display
                opButton.setOnAction(e -> {
                    display.appendText(op + "(");
                    // Hide the advanced panel after selecting a process
                    hideAdvancedPanel();
                });
                opButton.getStyleClass().add("adv-button");
                buttonsPane.getChildren().add(opButton);
            }
            advancedPanel.getChildren().addAll(sectionLabel, buttonsPane);
        }
    }

    // This method handles user input from calculator buttons
    private void handleInput(String input) {
        if (input.equals("=")) {
            try {

                // Get the expression from the display
                String expression = display.getText();

                // Check if the expression contains boolean logic operations
                boolean conBoolfunc
                        = expression.contains("palindrome(")
                        || expression.contains("armstrong")
                        || expression.contains("prime");

                // Evaluate the expression
                String result = String.valueOf(eval(display.getText()));
                lastAnswer = Double.parseDouble(result);  // Store result for future use
                display.setText(result);

                // If it's a boolean logic function, display true or false
                if (conBoolfunc && (result.equals("1.0") || result.equals("1"))) {
                    display.setText("True");
                } else if (conBoolfunc && (result.equals("0.0") || result.equals("0"))) {
                    display.setText("false");
                } else {
                    display.setText(result);
                }

            } catch (Exception ex) {
                // Show error on exception
                display.setText("Error:");
            }
        } else if (input.equals("C")) {
            // Clear the display
            display.clear();
        } else if (input.equals("Ans")) {
            // Insert the last calculated result
            display.appendText(Double.toString(lastAnswer));

        } else if (input.equals("±")) {
            try {
                // Negate the current number
                double value = Double.parseDouble(display.getText().trim());
                display.setText(String.valueOf(-value));
            } catch (Exception ex) {
                display.setText("");
            }
        } else if (input.equals("%")) {
            // Convert to percentage
            try {
                double value = Double.parseDouble(display.getText().trim());
                display.setText(String.valueOf(value / 100));
            } catch (Exception ex) {
                display.setText("");
            }
        } else if (input.equals("^")) {
            // Add exponentiation symbol
            display.appendText("^");
        } else if (input.equals("√")) {
            // Add exponentiation sqrt
            display.appendText("sqrt(");
            // Insert square root function
        } else if (input.equals("log")) {
            // Add exponentiation log
            display.appendText("log(");
        } else if (input.equals("ln")) {
            // Add exponentiation ln
            display.appendText("ln(");
        } else if (input.equals("exp")) {
            // Add exponentiation exp
            display.appendText("exp(");
        } else if (input.equals("⌫")) {
            // Remove the last character
            String text = display.getText();
            if (!text.isEmpty()) {
                display.setText(text.substring(0, text.length() - 1));
            }
        } else if (input.equals(",")) {
            // Add comma for multi-value functions
            display.appendText(",");
        } else if (input.equals("Exit")) {
            // Exit the application
            Platform.exit();
        } else {
            // Default: append input to the display
            display.appendText(input);
        }
    }

    // ================== Expression Evaluation Functions ===================
    // Preprocess the expression to convert percentages into valid mathematical expressions
    private String PreprocessExpression(String expr) {
        return expr.replaceAll("(\\d+(\\.\\d+)?)%", "($1/100)");
    }

    //advanced expressions buttons on the side panel
    // The main evaluation function for mathematical expressions
    // It applies all supported functions and parses the final expression manually
    private double eval(String expr) {
        expr = PreprocessExpression(expr);

        expr = handleInverseTrigFunctions(expr);

        // Apply unary functions like sin, cos, log, etc.
        expr = applyFunc(expr, "sin", x -> Math.sin(Math.toRadians(x)));
        expr = applyFunc(expr, "cos", x -> Math.cos(Math.toRadians(x)));
        expr = applyFunc(expr, "tan", x -> Math.tan(Math.toRadians(x)));
        expr = applyFunc(expr, "sqrt", Math::sqrt);
        expr = applyFunc(expr, "log", Math::log10);
        expr = applyFunc(expr, "ln", Math::log);
        expr = applyFunc(expr, "exp", Math::exp);
        expr = applyFunc(expr, "sqrt", Math::sqrt);
        expr = applyFunc(expr, "cbrt", Math::cbrt);
        expr = applyFunc(expr, "square", x -> Math.pow(x, 2));
        expr = applyFunc(expr, "cube", x -> Math.pow(x, 3));
        expr = applyFunc(expr, "round", x -> (double) Math.round(x));
        expr = applyFunc(expr, "ceil", Math::ceil);
        expr = applyFunc(expr, "floor", Math::floor);

        // Apply multi-value functions like avg, min, max, etc
        expr = applyMultiFunc(expr, "avg", this::average);
        expr = applyMultiFunc(expr, "min", this::min);
        expr = applyMultiFunc(expr, "max", this::max);
        expr = applyMultiFunc(expr, "GCD", this::GCD);
        expr = applyMultiFunc(expr, "LCM", this::LCM);

        // Apply boolean functions returning true/false
        expr = applyBooleanFunc(expr, "palindrome", this::isPalindrome);
        expr = applyBooleanFunc(expr, "armstrong", this::isArmstrong);
        expr = applyBooleanFunc(expr, "prime", this::isPrime);

        // Begin manual parsing of mathematical expression
        String finalExpr = expr;

        return new Object() {
            int pos = -1, ch; // pos = current position, ch = current character
            String expr = finalExpr;

            // Advance to next character
            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            // Consume current character if it matches expected one
            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            // Entry point for parsing
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            // Parse expressions with '+' and '-'
            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) {
                        x += parseTerm();
                    } else if (eat('-')) {
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            // Parse terms with '*' and '/'
            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('*')) {
                        x *= parseFactor();
                    } else if (eat('/')) {
                        x /= parseFactor();
                    } else {
                        return x;
                    }
                }
            }

            // Parse numbers, parenthesis, unary signs, and power '^'
            double parseFactor() {
                //Parse numbers
                if (eat('+')) {
                    return parseFactor();
                }
                if (eat('-')) {
                    return -parseFactor();
                }
                double x;

                // Parse parenthesis
                int startPos = pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');

                    // Parse unary signs .
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }
                    x = Double.parseDouble(expr.substring(startPos, pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                // Handle exponentiation
                if (eat('^')) {
                    double exponent = parseFactor();
                    x = Math.pow(x, exponent);
                }
                return x;
            }

        }.parse();
    }

    // ================== Helper functions to apply math functions in expression ===================
    // Unary functions (take one argument only)
    // Function to apply single-argument functions such as sin, cos, sqrt..
    private String applyFunc(String expr, String name, DoubleUnaryOperator op) {
        while (expr.contains(name + "(")) {

            // Extract the part inside the function parentheses
            int start = expr.indexOf(name + "(") + name.length() + 1;
            int end = expr.indexOf(")", start);

            // Exit if closing parenthesis is not found
            if (end == -1) {
                break;
            }
            String innerExpr = expr.substring(start, end);// Expression inside the parentheses
            double val;
            try {
                val = eval(innerExpr.trim());// Evaluate the inner expression to get a number
            } catch (Exception ex) {
                return "Error"; // If evaluation fails
            }
            double res;
            try {
                res = op.applyAsDouble(val);// Apply the mathematical function

                // Round very small results to zero
                if (Math.abs(res) < 1e-10) {
                    res = 0;
                }
                String full = name + "(" + innerExpr + ")";
                expr = expr.replace(full, Double.toString(res));
            } catch (Exception ex) {
                return "Error";
            }
        }
        return expr;
    }

    // Multi-value functions (like avg, min)
    // Function to apply multi-argument functions (comma-separated) such as avg, min, max
    private String applyMultiFunc(String expr, String name, Function<String, Double> op) {
        while (expr.contains(name + "(")) {
            int start = expr.indexOf(name + "(") + name.length() + 1;
            int end = expr.indexOf(")", start);
            if (end == -1) {
                break;
            }
            String args = expr.substring(start, end);// Extract the arguments
            double res = op.apply(args);// Apply the function to the arguments
            String full = name + "(" + args + ")";
            expr = expr.replace(full, Double.toString(res));
        }
        return expr;
    }

    // Boolean functions (return true/false like prime or palindrome)
    // Function to apply boolean-returning functions like prime, palindrome, armstrong
    private String applyBooleanFunc(String expr, String name, Function<Double, Boolean> func) {
        while (expr.contains(name + "(")) {
            int start = expr.indexOf(name + "(") + name.length() + 1;
            int end = expr.indexOf(")", start);
            if (end == -1) {
                break;
            }
            String inner = expr.substring(start, end).trim();
            double value;
            try {
                value = Double.parseDouble(inner);// Convert input to a number
            } catch (Exception e) {
                return "Error";
            }
            boolean result;
            try {
                result = func.apply(value);
            } catch (Exception e) {
                return "Error";
            }
            String full = name + "(" + inner + ")";

            expr = expr.replace(full, result ? "1" : "0");
        }
        return expr;
    }

    //
    // ================== Handle Inverse Trigonometric Functions  ================================
    // 
    //* This method processes all inverse trigonometric functions (asin, acos, atan)
    //* by detecting them in the expression, evaluating their arguments, and replacing
    //* them with their calculated degree result.
    private String handleInverseTrigFunctions(String expr) {
        expr = applySafeFunc(expr, "asin", x -> Math.toDegrees(Math.asin(x)));
        expr = applySafeFunc(expr, "acos", x -> Math.toDegrees(Math.acos(x)));
        expr = applySafeFunc(expr, "atan", x -> Math.toDegrees(Math.atan(x)));
        return expr;
    }

    //* A safer version of applyFunc that supports nested expressions inside a function.
    //* It finds the matching closing parenthesis, evaluates the inner content,
    //* applies the function, and replaces it in the original expression.
    private String applySafeFunc(String expr, String name, DoubleUnaryOperator op) {
        while (expr.contains(name + "(")) {
            int startIndex = expr.indexOf(name + "(");
            int openParen = startIndex + name.length() + 1;
            int closeParen = findMatchingParenthesis(expr, openParen - 1);
            if (closeParen == -1) {
                break;
            }
            String innerExpr = expr.substring(openParen, closeParen);
            double val;
            try {
                val = eval(innerExpr.trim());
            } catch (Exception ex) {
                throw new RuntimeException("Invalid " + name + " input: " + innerExpr);
            }
            double result = op.applyAsDouble(val);
            String full = expr.substring(startIndex, closeParen + 1);
            expr = expr.replace(full, Double.toString(result));
        }
        return expr;
    }

    //* Utility method to find the matching closing parenthesis in an expression
    //* given the index of an opening parenthesis.
    private int findMatchingParenthesis(String expr, int openIndex) {
        int count = 0;
        for (int i = openIndex; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') {
                count++;
            } else if (expr.charAt(i) == ')') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    // ================== Scientific Operation Functions ===================
    // Calculate the average of numbers separated by commas
    private double average(String input) {
        return Arrays.stream(input.split(",")).mapToDouble(Double::parseDouble).average().orElse(0);
    }

    // Calculate the minimum number
    private double min(String input) {
        return Arrays.stream(input.split(",")).mapToDouble(Double::parseDouble).min().orElse(0);
    }

    // Calculate the maximum number
    private double max(String input) {
        return Arrays.stream(input.split(",")).mapToDouble(Double::parseDouble).max().orElse(0);
    }

    // Calculate the Greatest Common Divisor (GCD)
    private double GCD(String input) {
        String[] parts = input.split(",");
        int a = Integer.parseInt(parts[0].trim());
        int b = Integer.parseInt(parts[1].trim());
        return computeGCD(a, b);
    }

    // Calculate the Least Common Multiple (LCM)
    private double LCM(String input) {
        String[] parts = input.split(",");
        int a = Integer.parseInt(parts[0].trim());
        int b = Integer.parseInt(parts[1].trim());
        return computeLCM(a, b);
    }

    // Euclidean algorithm for computing GCD
    private int computeGCD(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    // Compute LCM using GCD
    private int computeLCM(int a, int b) {
        return Math.abs(a * b) / computeGCD(a, b);
    }

    // Check if a number is a Palindrome (reads the same forward and backward)
    private boolean isPalindrome(double number) {
        String str = String.valueOf((int) number);
        if (str.length() < 3) {

            // Show warning alert if number has less than 3 digits
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("ﺗﻨﺒﻴﻪ");
            alert.setHeaderText(null);
            alert.setContentText("A number of 3 or more digits must be entered for the Palindrome operation");
            alert.showAndWait();
            return false;
        }
        return str.equals(new StringBuilder(str).reverse().toString());
    }

    // Check if number is Armstrong number
    private boolean isArmstrong(double number) {
        int n = (int) number;
        int temp = n, sum = 0, digits = String.valueOf(n).length();
        while (temp != 0) {
            int digit = temp % 10;
            sum += Math.pow(digit, digits);// Raise each digit to the power of number of digits
            temp /= 10;
        }
        return sum == n;
    }

    // Check if number is Prime
    private boolean isPrime(double number) {
        int n = (int) number;
        if (n < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // Entry point of the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }

}
