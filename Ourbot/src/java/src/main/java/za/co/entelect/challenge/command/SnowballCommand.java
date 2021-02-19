package za.co.entelect.challenge.command;


public class SnowballCommand implements Command {

    private int x;
    private int y;

    public SnowballCommand(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String render() {
        return String.format("snowball %d %d", this.x, this.y);
    }
}