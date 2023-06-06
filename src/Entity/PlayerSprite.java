package Entity;
import GameLogic.GameScreen;
import GameLogic.InputHandler;
import Utilities.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class PlayerSprite extends Sprite
{
    GameScreen gameScreen;
    InputHandler inputHandler;

    public final int screenX;
    public final int screenY;

    public PlayerSprite(GameScreen gameScreen, InputHandler inputHandler)
    {
        this.gameScreen = gameScreen;
        this.inputHandler = inputHandler;

        screenX = (GameScreen.SCREEN_WIDTH / 2) - (GameScreen.TILE_SIZE / 2);
        screenY = (gameScreen.SCREEN_HEIGHT / 2) - (GameScreen.TILE_SIZE / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
    }

    public void setDefaultValues()
    {
        worldX = GameScreen.TILE_SIZE * 2;
        worldY = GameScreen.TILE_SIZE * 2;
        speed = 3;
        spriteFiles = Util.loadFileToHashMap();
        direction = "down";
    }

    public void update()
    {
        if(inputHandler.isUp || inputHandler.isDown || inputHandler.isLeft || inputHandler.isRight)
        {
            if(inputHandler.isUp)
            {
                direction = "up";

            }
            else if(inputHandler.isDown)
            {
                direction = "down";
            }
            else if(inputHandler.isRight)
            {
                direction = "right";
            }
            else if(inputHandler.isLeft)
            {
                direction = "left";
            }

            collisionOn = false;
            gameScreen.checkCollision.checkTile(this);

            int objIndex = gameScreen.checkCollision.checkObject(this,true);
            if(objIndex == 999)
            {
                movePlayer();
            }
            else
            {
                if(!checkNearbyPieces(objIndex))
                {
                    objectInteraction(objIndex);
                }
            }

            spriteCounter++;

            if(spriteCounter > 20) // for every 20 frames
            {
                if(spriteNum == 0)
                {
                    spriteNum = 1;
                }
                else if(spriteNum == 1)
                {
                    spriteNum = 2;
                }
                else if (spriteNum == 2)
                {
                    spriteNum = 3;
                }
                else if (spriteNum == 3)
                {
                    spriteNum = 4;
                }
                else if (spriteNum == 4)
                {
                    spriteNum = 5;
                }
                else if (spriteNum == 5)
                {
                    spriteNum = 0;
                }
                spriteCounter = 0;
            }
        }
        else
        {
            if(direction.equals("down") )
            {
                direction = "idle";
            }
            else if(direction.equals("up"))
            {
                direction = "idleTop";
            }
            else if(direction.equals("right"))
            {
                direction = "idleRight";
            }
            else if(direction.equals("left"))
            {
                direction = "idleLeft";
            }


            int objIndex = gameScreen.checkCollision.checkObjectIdle(this,true);
            if(objIndex != 999)
            {
                if(!checkNearbyPieces(objIndex))
                {
                    objectInteraction(objIndex);
                }
                else
                {
                    boolean[] possibleMoves = checkSurroundingPieces(objIndex);
                    // need to add another possibilites where you dont need to move character sprite while moving the objects
                    if(!possibleMoves[0] && inputHandler.upArrow)
                    {
                        worldY -= GameScreen.TILE_SIZE;
                        gameScreen.obj[objIndex].worldY -= GameScreen.TILE_SIZE;
                    }
                    else if(!possibleMoves[1] && inputHandler.downArrow)
                    {
                        worldY += GameScreen.TILE_SIZE;
                        gameScreen.obj[objIndex].worldY += GameScreen.TILE_SIZE;
                    }
                    else if(!possibleMoves[2] && inputHandler.rightArrow)
                    {
                        worldX += GameScreen.TILE_SIZE;
                        gameScreen.obj[objIndex].worldX += GameScreen.TILE_SIZE;
                    }
                    else if(!possibleMoves[3] && inputHandler.leftArrow)
                    {
                        worldX -= GameScreen.TILE_SIZE;
                        gameScreen.obj[objIndex].worldX -= GameScreen.TILE_SIZE;
                    }
                }
            }

            spriteCounter++;
            if(spriteCounter > 20) // for every 20 frames (1/3 s)
            {
                if(spriteNum == 0)
                {
                    spriteNum = 1;
                }
                else if(spriteNum == 1)
                {
                    spriteNum = 2;
                }
                else if (spriteNum == 2)
                {
                    spriteNum = 3;
                }
                else if (spriteNum == 3)
                {
                    spriteNum = 0;
                }

                spriteCounter = 0;
            }
        }
    }

    private void movePlayer()
    {
        if(collisionOn == false)
        {
            switch (direction)
            {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "right" -> worldX += speed;
                case "left" -> worldX -= speed;
            }
        }
    }
    private void movePlayer(int objectIndex)
    {
        if(collisionOn)
        {
            switch (direction)
            {
                case "idleTop", "up" ->
                {
                    if (inputHandler.fKey || inputHandler.upArrow)
                    {
                        worldY -= speed;
                        gameScreen.obj[objectIndex].worldY -= GameScreen.TILE_SIZE;
                    }
                }
                case "idle", "down" ->
                {
                    if (inputHandler.fKey || inputHandler.downArrow)
                    {
                        worldY += speed;
                        gameScreen.obj[objectIndex].worldY += GameScreen.TILE_SIZE;
                    }
                }
                case "idleRight", "right" ->
                {
                    if (inputHandler.fKey || inputHandler.rightArrow)
                    {
                        worldX += speed;
                        gameScreen.obj[objectIndex].worldX += GameScreen.TILE_SIZE;
                    }
                }
                case "idleLeft", "left" ->
                {
                    if (inputHandler.fKey || inputHandler.leftArrow)
                    {
                        worldX -= speed;
                        gameScreen.obj[objectIndex].worldX -= GameScreen.TILE_SIZE;
                    }
                }
            }
        }
    }
    private void objectInteraction(int objectIndex)
    {
        if(objectIndex != 999)
        {
            movePlayer(objectIndex);
        }
    }

    private boolean checkNearbyPieces(int currentPiece)
    {
        int currentPieceX = gameScreen.obj[currentPiece].worldX;
        int currentPieceY = gameScreen.obj[currentPiece].worldY;
        boolean cantMove = false;
        switch (direction)
        {
            case "idleTop", "up" ->
            {
                for (int i = 0; i < gameScreen.obj.length; i++)
                {
                    if (gameScreen.obj[i] != null && i != currentPiece)
                    {
                        if (gameScreen.obj[i].worldX == currentPieceX && gameScreen.obj[i].worldY == currentPieceY - GameScreen.TILE_SIZE)
                        {
                            cantMove = true;
                            System.out.println("cant move" + direction);
                            System.out.println(gameScreen.obj[i].worldX + " " + gameScreen.obj[i].worldY + " " + currentPieceX + " " + currentPieceY);
                            break;
                        }
                    }
                }
            }
            case "idle", "down" ->
            {
                for (int i = 0; i < gameScreen.obj.length; i++)
                {
                    if (gameScreen.obj[i] != null && i != currentPiece)
                    {
                        if (gameScreen.obj[i].worldX == currentPieceX && gameScreen.obj[i].worldY == currentPieceY + GameScreen.TILE_SIZE)
                        {
                            cantMove = true;
                            System.out.println("cant move" + direction);
                            System.out.println(gameScreen.obj[i].worldX + " " + gameScreen.obj[i].worldY + " " + currentPieceX + " " + currentPieceY);
                            break;
                        }
                    }
                }
            }
            case "idleRight", "right" ->
            {
                for (int i = 0; i < gameScreen.obj.length; i++)
                {
                    if (gameScreen.obj[i] != null && i != currentPiece)
                    {
                        if (gameScreen.obj[i].worldY == currentPieceY && gameScreen.obj[i].worldX == currentPieceX + GameScreen.TILE_SIZE)
                        {
                            cantMove = true;
                            System.out.println("cant move" + direction + " " + i);
                            System.out.println(gameScreen.obj[i].worldX + " " + gameScreen.obj[i].worldY + " " + currentPieceX + " " + currentPieceY);
                            break;
                        }
                    }
                }
            }
            case "idleLeft", "left" ->
            {
                for (int i = 0; i < gameScreen.obj.length; i++)
                {
                    if (gameScreen.obj[i] != null && i != currentPiece)
                    {
                        if (gameScreen.obj[i].worldY == currentPieceY && gameScreen.obj[i].worldX == currentPieceX - GameScreen.TILE_SIZE)
                        {
                            cantMove = true;
                            System.out.println("cant move" + direction);
                            System.out.println(gameScreen.obj[i].worldX + " " + gameScreen.obj[i].worldY + " " + currentPieceX + " " + currentPieceY);
                            break;
                        }
                    }
                }
            }
        }
        return cantMove;

    }

    private boolean[] checkSurroundingPieces(int currentPiece)
    {
        int currentPieceX = gameScreen.obj[currentPiece].worldX;
        int currentPieceY = gameScreen.obj[currentPiece].worldY;
        boolean upBlocked = false, downBlocked = false, rightBlocked = false, leftBlocked = false;
        boolean [] res = new boolean[4];

        for(int i = 0; i < gameScreen.obj.length; i++)
        {
            if(gameScreen.obj[i] != null && i != currentPiece)
            {
                if(gameScreen.obj[i].worldX == currentPieceX && gameScreen.obj[i].worldY == currentPieceY - GameScreen.TILE_SIZE)
                {
                    upBlocked = true;
                    i = (i != gameScreen.obj.length) ? i++ : i;
                }
                if(gameScreen.obj[i].worldX == currentPieceX && gameScreen.obj[i].worldY == currentPieceY + GameScreen.TILE_SIZE)
                {
                    downBlocked = true;
                    i = (i != gameScreen.obj.length) ? i++ : i;
                }
                if(gameScreen.obj[i].worldY == currentPieceY && gameScreen.obj[i].worldX == currentPieceX + GameScreen.TILE_SIZE)
                {
                    rightBlocked = true;
                    i = (i != gameScreen.obj.length) ? i++ : i;
                }
                if(gameScreen.obj[i].worldY == currentPieceY && gameScreen.obj[i].worldX == currentPieceX - GameScreen.TILE_SIZE)
                {
                    leftBlocked = true;
                    i = (i != gameScreen.obj.length) ? i++ : i;
                }
            }
            if(upBlocked && downBlocked && rightBlocked && leftBlocked)
            {
                break;
            }
        }
        res[0] = upBlocked;
        res[1] = downBlocked;
        res[2] = rightBlocked;
        res[3] = leftBlocked;
        return res;
    }



    public void draw(Graphics2D g2d)
    {
        Image image = null;
        ArrayList<Image> temp = getSpritesFromDirections(Util.loadFileToHashMap(),direction);
        int x = screenX;
        int y = screenY;
        if(screenX > worldX)
        {
            x  = worldX;
        }
        if(screenY > worldY)
        {
            y = worldY;
        }
        int rightWorldBorder = GameScreen.SCREEN_WIDTH - screenX;
        if(rightWorldBorder > gameScreen.worldWidth - worldX)
        {
            x = GameScreen.SCREEN_WIDTH - (gameScreen.worldWidth - worldX);
        }
        int bottomWorldBorder = GameScreen.SCREEN_HEIGHT - screenY;
        if(bottomWorldBorder > gameScreen.worldWidth - worldY)
        {
            y = GameScreen.SCREEN_HEIGHT - (gameScreen.worldHeight - worldY);
        }

        if((direction == "idle" || direction == "idleTop" || direction == "idleRight" || direction == "idleLeft") && spriteNum > 3)
        {
            spriteNum = 0;
            g2d.drawImage(temp.get(spriteNum), x, y,GameScreen.TILE_SIZE,GameScreen.TILE_SIZE,null);
        }
        else
        {
            g2d.drawImage(temp.get(spriteNum), x, y,GameScreen.TILE_SIZE,GameScreen.TILE_SIZE,null);
        }
    }

    private ArrayList<Image> getSpritesFromDirections(Map<String, ArrayList<Image>> spriteFiles, String direction)
    {
        return switch (direction)
                {
                    case "up" -> spriteFiles.get("WalkUp");
                    case "down" -> spriteFiles.get("Walk");
                    case "right" -> spriteFiles.get("WalkRight");
                    case "left" -> spriteFiles.get("WalkLeft");
                    case "idle" -> spriteFiles.get("Idle");
                    case "idleTop" -> spriteFiles.get("IdleTop");
                    case "idleRight" -> spriteFiles.get("IdleRight");
                    case "idleLeft" -> spriteFiles.get("IdleLeft");
                    default -> null;
                };
    }
}
