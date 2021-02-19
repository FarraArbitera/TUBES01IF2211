package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Direction;

import java.util.*;
import java.util.stream.Collectors;
import java.lang.Math;

public class Bot {

    private Random random;
    private GameState gameState;
    private Opponent opponent;
    private MyWorm currentWorm;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.opponent = gameState.opponents[0];
        this.currentWorm = getCurrentWorm(gameState);
    }

    private MyWorm getCurrentWorm(GameState gameState) {
        return Arrays.stream(gameState.myPlayer.worms)
                .filter(myWorm -> myWorm.id == gameState.currentWormId)
                .findFirst()
                .get();
    }

    public Command run() {
        Worm enemyWorm = getFirstWormInRange();
        Worm enemyWormCanLob = getFirstWormInRangeLob();

        if (enemyWormCanLob != null && currentWorm.id != 1) {
            if (currentWorm.id == 2) {
                if (canBanana(currentWorm, enemyWormCanLob)) {
                    return new BananaBombCommand(enemyWormCanLob.position.x, enemyWormCanLob.position.y);
                }
            }
        }

        if (enemyWorm != null) {
            if (currentWorm.id == 3) {
                if (canSnowBall(currentWorm, enemyWorm)) {
                    return new SnowballCommand(enemyWorm.position.x, enemyWorm.position.y);
                }
            }
            Direction direction = resolveDirection(currentWorm.position, enemyWorm.position);
            if (canShoot(currentWorm, enemyWorm)) {
                return new ShootCommand(direction);
            }
        }

        for (int i =0; i < gameState.myPlayer.worms.length; i++){
            Worm enemy = getFirstWormInRange(gameState.myPlayer.worms[i]);
            MyWorm cacinglokal = gameState.myPlayer.worms[i]; 
            if (enemy != null && cacinglokal.id != currentWorm.id && cacinglokal.health > 0 && cacinglokal.roundsUntilUnfrozen==0){
                if (cacinglokal.id == 2){
                    if (canBanana(cacinglokal,enemy)){
                        return new SelectCommand(cacinglokal.id, String.format("banana %d %d", enemy.position.x, enemy.position.y));
                    }
                }
                if (cacinglokal.id == 3){
                    if (canSnowBall(cacinglokal,enemy)){
                        return new SelectCommand(cacinglokal.id, String.format("snowball %d %d", enemy.position.x,enemy.position.y));
                    }
                }
                if (canShoot(cacinglokal,enemy)){
                    Direction direction = resolveDirection(cacinglokal.position, enemy.position);
                    return new SelectCommand(cacinglokal.id, String.format("shoot %s", direction.name()));
                }
            }
        }


        List<Cell> surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        List<Cell> farSurroundingBlocks = getFarSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        //int cellIdx = random.nextInt(surroundingBlocks.size());
        int CenterCell = gameState.mapSize / 2;
        /*
        if (Arrays.stream(gameState.map).f//anyMatch(x -> x. != null)){

            Cell nearest = Arrays.stream(gameState.map).filter(c -> c.powerUp != null).sorted((a,b) -> euclideanDistance(a.x,a.y,currentWorm.position.x,currentWorm.position.y) - euclideanDistance(b.x,b.y,currentWorm.position.x,currentWorm.position.y))[0];
            if (euclideanDistance(nearest.x,nearest.y,currentWorm.position.x,currentWorm.position.y) > euclideanDistance(CenterCell,CenterCell,currentWorm.position.x,currentWorm.position.y)){
                for (int i = 0; i < surroundingBlocks.size(); i++) {
                    Cell block = surroundingBlocks.get(i);
                    if (currentWorm.position.x <= CenterCell && currentWorm.position.y <= CenterCell) {
                        if (block.x > currentWorm.position.x && block.y > currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                    if (currentWorm.position.x > CenterCell && currentWorm.position.y <= CenterCell) {
                        if (block.x < currentWorm.position.x && block.y > currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                    if (currentWorm.position.x <= CenterCell && currentWorm.position.y > CenterCell) {
                        if (block.x > currentWorm.position.x && block.y < currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                    if (currentWorm.position.x > CenterCell && currentWorm.position.y > CenterCell) {
                        if (block.x < currentWorm.position.x && block.y < currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                }
            }
            else{
                for (int i = 0; i < surroundingBlocks.size(); i++) {
                    Cell block = surroundingBlocks.get(i);
                    if (currentWorm.position.x <= nearest.x && currentWorm.position.y <= nearest.y) {
                        if (block.x > currentWorm.position.x && block.y > currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                    if (currentWorm.position.x > nearest.x && currentWorm.position.y <= nearest.y) {
                        if (block.x < currentWorm.position.x && block.y > currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                    if (currentWorm.position.x <= nearest.x && currentWorm.position.y > nearest.y) {
                        if (block.x > currentWorm.position.x && block.y < currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                    if (currentWorm.position.x > nearest.x && currentWorm.position.y > nearest.y) {
                        if (block.x < currentWorm.position.x && block.y < currentWorm.position.y) {
                            if (block.type == CellType.AIR) {
                                return new MoveCommand(block.x, block.y);
                            } else if (block.type == CellType.DIRT) {
                                return new DigCommand(block.x, block.y);
                            }
                        }
                    }
                }
            }

        
                /* OLD CODE
            if (block.type == CellType.AIR && (block.x != currentWorm.position.x && block.y != currentWorm.position.y)) {
                return new MoveCommand(block.x, block.y);
            } else if (block.type == CellType.DIRT && (block.x != currentWorm.position.x && block.y != currentWorm.position.y)) {
                return new DigCommand(block.x, block.y);
                 */
        //}


        for (int i = 0; i < surroundingBlocks.size(); i++) {
            Cell block = surroundingBlocks.get(i);
            //Cell farBlock = farSurroundingBlocks.get(i);
            //if (farBlock.powerUp.value != 0) {
                //return new DoNothingCommand();
                /*
                if (currentWorm.position.x <= CenterCell && currentWorm.position.y <= CenterCell) {
                    if (block.x > currentWorm.position.x && block.y > currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
                if (currentWorm.position.x > CenterCell && currentWorm.position.y <= CenterCell) {
                    if (block.x < currentWorm.position.x && block.y > currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
                if (currentWorm.position.x <= CenterCell && currentWorm.position.y > CenterCell) {
                    if (block.x > currentWorm.position.x && block.y < currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
                if (currentWorm.position.x > CenterCell && currentWorm.position.y > CenterCell) {
                    if (block.x < currentWorm.position.x && block.y < currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
                 */
            //}else{
                if (currentWorm.position.x <= CenterCell && currentWorm.position.y <= CenterCell) {
                    if (block.x > currentWorm.position.x && block.y > currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
                if (currentWorm.position.x > CenterCell && currentWorm.position.y <= CenterCell) {
                    if (block.x < currentWorm.position.x && block.y > currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
                if (currentWorm.position.x <= CenterCell && currentWorm.position.y > CenterCell) {
                    if (block.x > currentWorm.position.x && block.y < currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
                if (currentWorm.position.x > CenterCell && currentWorm.position.y > CenterCell) {
                    if (block.x < currentWorm.position.x && block.y < currentWorm.position.y) {
                        if (block.type == CellType.AIR) {
                            return new MoveCommand(block.x, block.y);
                        } else if (block.type == CellType.DIRT) {
                            return new DigCommand(block.x, block.y);
                        }
                    }
                }
            }
        //}

        return new DoNothingCommand();
    }

    public boolean canBanana(MyWorm cacingku,Worm enemy){
        if (euclideanDistance(cacingku.position.x, cacingku.position.y, enemy.position.x, enemy.position.y) <= cacingku.bananaBombs.range  && cacingku.bananaBombs.count > 0) {
            boolean safe = true;
            for (int i = 0; i < gameState.myPlayer.worms.length; i++) {
                if (euclideanDistance(AlliesPos(i).x, AlliesPos(i).y, enemy.position.x, enemy.position.y) <= cacingku.bananaBombs.damageRadius)// && gameState.myPlayer.worms[i].health > 0)
                    safe = false;
            }
            if (safe || cacingku.health >= 60) {
                return true;
            }
        }
        return false;
    }

    public boolean canSnowBall(MyWorm cacingku, Worm enemy){
        if (euclideanDistance(cacingku.position.x, cacingku.position.y, enemy.position.x, enemy.position.y) <= cacingku.snowballs.range && cacingku.snowballs.count > 0) {
            boolean safe = true;
            for (int i = 0; i < gameState.myPlayer.worms.length; i++) {
                if (euclideanDistance(AlliesPos(i).x, AlliesPos(i).y, enemy.position.x, enemy.position.y) <= cacingku.snowballs.freezeRadius)// && gameState.myPlayer.worms[i].health > 0)
                    safe = false;
            }
            if ((safe || cacingku.health >= 40) && enemy.roundsUntilUnfrozen == 0){
                return true;
            }
        }
        return false;
    }

    public boolean canShoot(MyWorm cacingku, Worm enemy){
        Direction direction = resolveDirection(cacingku.position, enemy.position);
            boolean safe = true;
            for (int i = 0; i < gameState.myPlayer.worms.length; i++) {
                if (cacingku.id != gameState.myPlayer.worms[i].id) {
                    if (direction == resolveDirection(cacingku.position, AlliesPos(i)) && gameState.myPlayer.worms[i].health > 0) {
                        safe = false;
                    }
                }
            }
        return safe;
    }

    private Worm getFirstWormInRange() {

        Set<String> cells = constructFireDirectionLines(currentWorm.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition) && enemyWorm.health > 0) {
                return enemyWorm;
            }
        }

        return null;
    }

    private Worm getFirstWormInRange(MyWorm myCacing) {

        Set<String> cells = constructFireDirectionLines(myCacing,myCacing.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition) && enemyWorm.health > 0) {
                return enemyWorm;
            }
        }

        return null;
    }

    private Worm getFirstWormInRangeLob() {
        Set<String> cells = constructFireDirectionLobs(5)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition) && enemyWorm.health > 0) {
                if (currentWorm.id == 3){
                    for (MyWorm myCacing : gameState.myPlayer.worms){
                        if (getFirstWormInRange(myCacing) == enemyWorm){
                            return enemyWorm;    
                        }
                    }
                }else{
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
    private List<List<Cell>> constructFireDirectionLines(MyWorm cacingku, int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {

                int coordinateX = cacingku.position.x + (directionMultiplier * direction.x);
                int coordinateY = cacingku.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(cacingku.position.x, cacingku.position.y, coordinateX, coordinateY) > range) {
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

    private List<List<Cell>> constructFireDirectionLobs(int range) {
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
                if (cell.type == CellType.DEEP_SPACE || cell.type == CellType.LAVA) {
                    break;
                }

                directionLine.add(cell);
            }
            directionLines.add(directionLine);
        }

        return directionLines;
    }

    private List<Cell> getSurroundingCells(int x, int y) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // Include current position for anticipate fire cells actually necessary
                if (isValidCoordinate(i, j) && x != i && y != j) {
                    cells.add(gameState.map[j][i]);
                }
            }
        }

        return cells;
    }
    private List<Cell> getFarSurroundingCells(int x, int y) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = x - 10; i <= x + 10; i++) {
            for (int j = y - 10; j <= y + 10; j++) {
                // Include current position for anticipate fire cells actually necessary
                if (isValidCoordinate(i, j) && x != i && y != j) {
                    cells.add(gameState.map[j][i]);
                }
            }
        }

        return cells;
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

    private Position AlliesPos(int ID) {
        return gameState.myPlayer.worms[ID].position;
    }
}
