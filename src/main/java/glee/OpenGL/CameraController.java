package glee.OpenGL;

import GLEngine.Core.Input.Input;
import GLEngine.Core.Input.KeyEvent;
import GLEngine.Core.Input.MouseEvent;
import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Objects.Components.Rendering.Camera;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Objects.Models.RenderSettings;
import GLEngine.Core.Window;
import GLEngine.Core.Worlds.WorldManager;
import glee.GLEngineConnection;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class CameraController extends Component {
    private Camera camRef;
    private float speed = 10;
    private float baseSpeed = 10;
    private float sprintSpeed = 20;
    private float mouseSpeed = 0.0005f;
    private long window;
    private boolean isWireframe = false;

    private boolean lockMouse = true;

    public CameraController(Camera camRef){
        this.camRef = camRef;

        Window.GetInstance().mouseCallbacks.add(new MouseEvent() {
            @Override
            public void mousePressed(int button) {
                if(button == GLFW_MOUSE_BUTTON_1){
                    Raycast(camRef.RayCastHitObject(50));
                }
            }
            @Override
            public void mouseReleased(int button) {
            }
        });

        Window.GetInstance().keyCallbacks.add(new KeyEvent() {
            @Override
            public void keyPressed(int key, int mods) {

            }

            @Override
            public void keyReleased(int key, int mods) {
                if(key == GLFW_KEY_TAB){
                    lockMouse = !lockMouse;

                    if(lockMouse){
                        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                    }else{
                        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    }
                }
                if(key == GLFW_KEY_1){
                    isWireframe = !isWireframe;
                    Window.GetInstance().setMasterRenderSettings(new RenderSettings(isWireframe,!isWireframe,true));
                }
            }
        });
    }

    @Override
    public void Update(float deltaTime){
        window = Window.GetInstance().getWindowHandle();
        CheckKeyboardInput(window, deltaTime, camRef.getDirectionFacingVector(), camRef.getRightVector());
        if(lockMouse) CheckMouseInput();
    }

    private void CheckMouseInput() {
        DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yPos = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(window, xPos, yPos);
        camRef.addHorizAngle((float) (mouseSpeed * (camRef.getHalfWidth()-xPos.get(0))));
        camRef.addVertAngle((float) (mouseSpeed * (camRef.getHalfHeight()- yPos.get(0))));
        glfwSetCursorPos(window, camRef.getHalfWidth(),camRef.getHalfHeight());
    }

    private void CheckKeyboardInput(long window, float deltaTime, Vector3f direction, Vector3f right) {
        if (Input.isKeyPressed(GLFW_KEY_W)) {
            setParentPosition(getParentPosition().add(direction.mul(deltaTime).mul(speed)));
        }
        if (Input.isKeyPressed(GLFW_KEY_S)) {
            setParentPosition(getParentPosition().sub(direction.mul(deltaTime).mul(speed)));
        }
        if (Input.isKeyPressed(GLFW_KEY_D)) {
            setParentPosition(getParentPosition().add(right.mul(deltaTime).mul(speed)));
        }
        if (Input.isKeyPressed(GLFW_KEY_A)) {
            setParentPosition(getParentPosition().sub(right.mul(deltaTime).mul(speed)));
        }
        if (Input.isKeyPressed(GLFW_KEY_SPACE)) {
            setParentPosition(getParentPosition().add(new Vector3f(0,1,0).mul(deltaTime).mul(speed)));
        }
        if (Input.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            setParentPosition(getParentPosition().add(new Vector3f(0,-1,0).mul(deltaTime).mul(speed)));
        }

        speed = (glfwGetKey( window, GLFW_KEY_LEFT_SHIFT ) == GLFW_PRESS)? sprintSpeed : baseSpeed;
    }

    private void Raycast(GameObject object){
        if(GameObject.isValid(object)){
            int goIndex = 0;
            for (GameObject go :
                    WorldManager.getCurrentWorld().GameObjects()) {
                if(object == go){
                    break;
                }
                goIndex++;
            }
            GLEngineConnection.writeFile("SELECTED:" + goIndex, "from");
        }
    }
}
