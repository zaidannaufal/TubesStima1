package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Direction;

import java.io.Console;
import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    private GameState gameState;
    private Opponent opponent;
    private MyWorm currentWorm;
    private int banana;
    private int snowball;
    private int strategi;

    public Bot(GameState gameState, int banana, int snowball, int strategi) {
        this.gameState = gameState;
        this.opponent = gameState.opponents[0];
        this.currentWorm = getCurrentWorm(gameState);
        this.banana = banana;
        this.snowball = snowball;
        this.strategi = strategi;
    }

    private MyWorm getCurrentWorm(GameState gameState) {

        return Arrays.stream(gameState.myPlayer.worms)
                .filter(myWorm -> myWorm.id == gameState.currentWormId)
                .findFirst()
                .get();
    }

    public int BananaValue(){
        return banana;
    }
    
    public int SnowballValue(){
        return snowball;
    }

    public int Strategi(){
        return strategi;
    }
    public Command run(){
        // move to middle
        MyPlayer player = gameState.myPlayer;
        Worm enemyWorm = getFirstWormInRange();
        if (enemyWorm != null) {
            Direction direction = resolveDirection(currentWorm.position, enemyWorm.position);
            if ((currentWorm.id == player.worms[1].id) && (CanBananaBomb(currentWorm.position, direction))){
                banana -= 1;
                return new BananaCommand(direction);
            } else if ((currentWorm.id == player.worms[2].id) && (CanSnowball(currentWorm.position, direction))){
                snowball -= 1;
                return new SnowballCommand(direction);
            }
            return new ShootCommand(direction);
        }
        
        if ((currentWorm.position.x >=15 || currentWorm.position.x <=18)  && (currentWorm.position.y >=15 || currentWorm.position.y <=18)&& strategi == 0){
            strategi+=1;
        } //else if((syarat sukses) && strategi sebelumnya){
            
    
        return currentWorm.id == player.worms[0].id ? worm1(strategi) : currentWorm.id == player.worms[1].id  ? worm2(strategi): worm3(strategi);    

        // Command whattodo = StrategyMovement(15, 15, currentWorm.position);
        // make formation
        // hunt
                
        // return whattodo;
        // return new DoNothingCommand();
    }

    private Command worm1(int strategi){
        if (strategi == 0){
            return StrategyMovement(15, 15,currentWorm.position);
        } else if (strategi == 1){  // strategi 1
            return StrategyHunt();
        }
        return new DoNothingCommand();
    }

    private Command worm2(int strategi){
        MyPlayer player = gameState.myPlayer;
        if (strategi == 0){
            return StrategyMovement(player.worms[0].position.x, player.worms[0].position.y, currentWorm.position);
        } else if (strategi == 1){
            return StrategyHunt();
        }
        return new DoNothingCommand();
        // else if (){
        // }
    }

    private Command worm3(int strategi){
        MyPlayer player = gameState.myPlayer;
        if (strategi == 0){
            return StrategyMovement(player.worms[0].position.x, player.worms[0].position.y, currentWorm.position);
        } else if (strategi == 1){
            return StrategyHunt();
        }
        return new DoNothingCommand();
    }



    private Worm getFirstWormInRange() {

        Set<String> cells = constructFireDirectionLines(currentWorm.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            if (enemyWorm.health >0){
                String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
                if (cells.contains(enemyPosition)) {
                    return enemyWorm;
                }
            }
        }
        return null;
    }

    private List<List<Cell>> constructFireDirectionLines(int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {

                int coordinateX = currentWorm.position.x + (directionMultiplier * direction.x);
                int coordinateY = currentWorm.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(currentWorm.position.x, currentWorm.position.y, coordinateX, coordinateY) > range) {
                    break;
                }

                Cell cell = gameState.map[coordinateY][coordinateX];
                if (cell.type != CellType.AIR) {
                    break;
                }

                directionLine.add(cell);
            }
            directionLines.add(directionLine);
        }

        return directionLines;
    }

    private int euclideanDistance(int aX, int aY, int bX, int bY) {
        return (int) (Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)));
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < gameState.mapSize
                && y >= 0 && y < gameState.mapSize;
    }

    private Direction resolveDirection(Position a, Position b) {
        StringBuilder builder = new StringBuilder();

        int verticalComponent = b.y - a.y;
        int horizontalComponent = b.x - a.x;

        if (verticalComponent < 0) {
            builder.append('N');
        } else if (verticalComponent > 0) {
            builder.append('S');
        }

        if (horizontalComponent < 0) {
            builder.append('W');
        } else if (horizontalComponent > 0) {
            builder.append('E');
        }

        return Direction.valueOf(builder.toString());
    }
    // private List<Cell> getSurroundingCells(int x, int y) {
    //     ArrayList<Cell> cells = new ArrayList<>();
    //     for (int i = x - 1; i <= x + 1; i++) {
    //         for (int j = y - 1; j <= y + 1; j++) {
    //             // Don't include the current position
    //             if (i != x && j != y && isValidCoordinate(i, j)) {
    //                 cells.add(gameState.map[j][i]);
    //             }
    //         }
    //     }

    //     return cells;
    // }

    // private boolean isValidCoordinate(int x, int y) {
    //     return x >= 0 && x < gameState.mapSize
    //             && y >= 0 && y < gameState.mapSize;
    // }

    private Command StrategyMovement(int x, int y, Position mypos) {
        int newx,newy;

        newx = mypos.x + perpindahan(x, mypos.x);
        newy = mypos.y + perpindahan(y, mypos.y);
        
        for (Worm wormgw : gameState.myPlayer.worms) {
            if(wormgw.id != currentWorm.id){
                if(wormgw.position.x == newx && wormgw.position.y ==newy){
                    return new DoNothingCommand();
                }else if ((newx == wormgw.position.x || newy == wormgw.position.y) && euclideanDistance(newx, newy, wormgw.position.x, wormgw.position.y) <= 2){
                    return new DoNothingCommand();
                } else if ((Math.abs(newx - wormgw.position.x) == Math.abs(newy - wormgw.position.y)) && euclideanDistance(newx, newy, wormgw.position.x, wormgw.position.y) <= 2){
                    return new DoNothingCommand();
                }
            }   
        }
        if (gameState.map[newy][newx].type== CellType.AIR){
            return new MoveCommand(newx, newy);
        } else if (gameState.map[newy][newx].type== CellType.DIRT){
            return new DigCommand(newx, newy);
        }

        return new DoNothingCommand();
    }

    private int perpindahan(int awal, int tujuan){
        return awal == tujuan ? 0 : awal > tujuan ? 1 : -1;
    }


// buat strategihunt
// cari posisi worm musuh
// bandingin ambil yang paling kecil
// terus tinggal strategymove ke worm dengan jarak paling kecil
    private Worm findNearestEnemy() {
        // int a = euclideanDistance(currentWorm.position.x, currentWorm.position.y, opponent.worms[0].position.x, opponent.worms[0].position.y);
        int a = 999;
        Worm nearestenemy = opponent.worms[0];
        for (Worm enemyWorm : opponent.worms) {
            if (euclideanDistance(currentWorm.position.x, currentWorm.position.y, enemyWorm.position.x, enemyWorm.position.y) < a && enemyWorm.health > 0) {
                a = euclideanDistance(currentWorm.position.x, currentWorm.position.y, enemyWorm.position.x, enemyWorm.position.y);
                nearestenemy = enemyWorm;
            }
        }
        return nearestenemy;
    }


    private Command StrategyHunt(){
        Worm wormEnemy = findNearestEnemy();
        return StrategyMovement(wormEnemy.position.x, wormEnemy.position.y, currentWorm.position);
    }


    private Boolean CanSnowball(Position mypos,Direction dir){
        int range = euclideanDistance(mypos.x, mypos.y, dir.x, dir.y);
        return snowball>0 && range > 2 && range <= 5;
    }

    private Boolean CanBananaBomb(Position mypos, Direction dir){
        int range = euclideanDistance(mypos.x, mypos.y, dir.x, dir.y);
        return banana > 0 && range > 2  && range <= 5;
    }
}
