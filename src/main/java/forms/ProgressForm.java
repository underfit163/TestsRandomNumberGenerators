package forms;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressForm {
    private final Stage dialogStage;

    public ProgressForm() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setWidth(248);
        dialogStage.setHeight(40);
        dialogStage.setTitle("ОЖИДАЙТЕ, ИДЕТ ТЕСТИРОВАНИЕ!");
        dialogStage.show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }
}