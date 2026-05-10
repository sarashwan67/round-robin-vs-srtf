public class InputValidator {

    public static boolean validate(String id, String arrival, String burst) {
        if (id == null || arrival == null || burst == null) {
            System.out.println("Fill all fields");
            return false;
        }

        String trimmedId = id.trim();
        String trimmedArrival = arrival.trim();
        String trimmedBurst = burst.trim();

        if (trimmedId.isEmpty() || trimmedArrival.isEmpty() || trimmedBurst.isEmpty()) {
            System.out.println("Fill all fields");
            return false;
        }

        try {
            int at = Integer.parseInt(trimmedArrival);
            int bt = Integer.parseInt(trimmedBurst);

            if (at < 0 || bt <= 0) {
                System.out.println("Arrival time must be >= 0 and burst time must be > 0");
                return false;
            }

        } catch (NumberFormatException e) {
            System.out.println("Numbers only");
            return false;
        }

        return true;
    }

    public static boolean validateQuantum(String quantum) {
        if (quantum == null || quantum.trim().isEmpty()) {
            System.out.println("Quantum is required");
            return false;
        }

        try {
            int q = Integer.parseInt(quantum.trim());
            if (q <= 0) {
                System.out.println("Quantum must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Quantum must be a number");
            return false;
        }

        return true;
    }
}