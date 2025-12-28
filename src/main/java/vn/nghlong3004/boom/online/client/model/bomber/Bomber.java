package vn.nghlong3004.boom.online.client.model.bomber;

import lombok.Getter;
import lombok.Setter;
import vn.nghlong3004.boom.online.client.constant.GameConstant;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Getter
@Setter
public class Bomber {

  private static final float DEFAULT_SPEED = 2.0f;
  private static final int DEFAULT_LIVES = 3;

  private final int playerIndex;
  private final Long userId;
  private final String displayName;
  private final BomberType bomberType;

  private float x;
  private float y;
  private float speed;

  private Direction direction;
  private BomberState state;

  private int lives;
  private boolean alive;

  private Bomber(Builder builder) {
    this.playerIndex = builder.playerIndex;
    this.userId = builder.userId;
    this.displayName = builder.displayName;
    this.bomberType = builder.bomberType;
    this.x = builder.spawnX;
    this.y = builder.spawnY;
    this.speed = DEFAULT_SPEED;
    this.direction = Direction.DOWN;
    this.state = BomberState.IDLE;
    this.lives = DEFAULT_LIVES;
    this.alive = true;
  }

  public void move(Direction newDirection) {
    this.direction = newDirection;
    this.state = BomberState.WALKING;
    this.x += newDirection.getDeltaX() * speed;
    this.y += newDirection.getDeltaY() * speed;
  }

  public void stop() {
    this.state = BomberState.IDLE;
  }

  public void die() {
    this.lives--;
    if (this.lives <= 0) {
      this.alive = false;
      this.state = BomberState.DEAD;
    } else {
      this.state = BomberState.DYING;
    }
  }

  public void respawn(float spawnX, float spawnY) {
    this.x = spawnX;
    this.y = spawnY;
    this.state = BomberState.IDLE;
    this.direction = Direction.DOWN;
  }

  public int getTileX() {
    return (int) ((x + GameConstant.TILE_SIZE / 2.0f) / GameConstant.TILE_SIZE);
  }

  public int getTileY() {
    return (int) ((y + GameConstant.TILE_SIZE / 2.0f) / GameConstant.TILE_SIZE);
  }

  public float getCenterX() {
    return x + GameConstant.TILE_SIZE / 2.0f;
  }

  public float getCenterY() {
    return y + GameConstant.TILE_SIZE / 2.0f;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private int playerIndex;
    private Long userId;
    private String displayName;
    private BomberType bomberType;
    private float spawnX;
    private float spawnY;

    public Builder playerIndex(int playerIndex) {
      this.playerIndex = playerIndex;
      return this;
    }

    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Builder displayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public Builder bomberType(BomberType bomberType) {
      this.bomberType = bomberType;
      return this;
    }

    public Builder spawnPosition(float x, float y) {
      this.spawnX = x;
      this.spawnY = y;
      return this;
    }

    public Bomber build() {
      return new Bomber(this);
    }
  }
}
