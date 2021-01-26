import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FLS_bot extends org.telegram.telegrambots.bots.TelegramLongPollingBot {
    private ArrayList<String> stepsList;
    private ArrayList<String> guidesList;
    private String helpNeeded;

    public FLS_bot() {
        stepsList = prepareStepsList();
        guidesList = prepareGuidesList();
        helpNeeded = "";
    }

    @Override
    public String getBotUsername() {
        return "First Level Support Bot";
    }

    @Override
    public String getBotToken() {
        return "1302957992:AAEMcrZNto-3agWxxrV8W0ohbJHrnzzuNgc";
    }

    @Override
    public void onUpdateReceived(Update upd) {
        if (upd.hasMessage()) {
            String userName = upd.getMessage().getFrom().getFirstName();
            String chatID = upd.getMessage().getChatId().toString();

            if (upd.getMessage().hasText()) {
                String incomingText = upd.getMessage().getText();
                int bulletNo = getBulletNo(incomingText);

                if (incomingText.equals("/start")) {
                    showStarterText(userName, chatID);
                }
                else if (incomingText.equals("/steps") || incomingText.equals("/guides")) {
                    if (incomingText.equals("/steps")) { helpNeeded = "steps"; }
                    else { helpNeeded = "guides"; }
                    showList(chatID, helpNeeded);
                }
                else if (incomingText.equals("/admin")) {
                    String outTxt = "What's the secret passcode?";
                    showText(chatID, outTxt);
                }
                else if (incomingText.equals("/help")) {
                    showBasicOptions(chatID);
                }
                else if (bulletNo != -1) {
                    getHelp(chatID, bulletNo);
                    showContinueText(chatID);
                } else {
                    showHelpText(chatID);
                }
            } else if (upd.getMessage().hasDocument()) {
                String outTxt = "Oh? You've sent me a document!";
                showText(chatID, outTxt);
            } else {
                String outTxt = "Sorry, I don't get understand this. /help";
                showText(chatID, outTxt);
            }
        }
    }

    private ArrayList<String> prepareStepsList() {
        ArrayList<String> stepsList = new ArrayList<String>();

        String path = new File("").getAbsolutePath() + "\\steps";
        File dirPath = new File(path);
        File filesList[] = dirPath.listFiles();

        for(var file: filesList) {
            stepsList.add(file.getName());
        }

        return stepsList;
    }

    private ArrayList<String> prepareGuidesList() {
        ArrayList<String> guidesList = new ArrayList<String>();

        String path = new File("").getAbsolutePath() + "\\guides";
        File dirPath = new File(path);
        File filesList[] = dirPath.listFiles();

        for(var file: filesList) {
            guidesList.add(file.getName());
        }

        return guidesList;
    }

    private int getBulletNo(String receivedText) {
        char[] receivedTextChar = receivedText.toCharArray();
        StringBuilder numericText = new StringBuilder();

        for(var c: receivedTextChar) {
            if (Character.isDigit(c)) {
                numericText.append(c);
            }
        }

        if (numericText.toString().equals("")) { return -1; }
        else { return Integer.parseInt(numericText.toString()); }
    }

    private void getHelp(String chatID, int bulletNo) {
        if (helpNeeded.equals("steps")) {
            String fileName = stepsList.get(bulletNo-1);
            String filePath = new File("").getAbsolutePath() + "\\steps\\" + fileName;
            String outgoingText = "";

            File file = new File(filePath);

            try { outgoingText = new Scanner(file).useDelimiter("\\Z").next(); }
            catch (FileNotFoundException e) { e.printStackTrace();
            }

            showText(chatID, outgoingText);
        }
        else if (helpNeeded.equals("guides")) {
            SendDocument sendDoc = new SendDocument();
            sendDoc.setChatId(chatID);
            String fileName = guidesList.get(bulletNo-1);
            String filePath = new File("").getAbsolutePath() + "\\guides\\" + fileName;
            sendDoc.setDocument(new InputFile(new File(filePath)));

            try { execute(sendDoc); } catch (TelegramApiException e) { e.printStackTrace(); }
        }
    }

    private void showText(String chatID, String txt) {
        SendMessage msg = new SendMessage();
        msg.setText(txt);
        msg.setChatId(chatID);

        try { execute(msg); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    private void showBasicOptions(String chatID) {
        String outText = "Tap the command below to let me know what kind of help you need:"
                + "\n/steps - get troubleshooting steps in text form."
                + "\n/guides - get guides as PDF files.";
        showText(chatID, outText);
    }

    private void showStarterText(String userName, String chatID) {
        String outText = "Hi " + userName + "! \uD83D\uDE0A";
        showText(chatID, outText);
        showBasicOptions(chatID);
    }

    private void showList(String chatID, String helpNeeded) {
        String outTxt = "Here's the list of " + helpNeeded + " I have:\n";
        int counter = 1;

        if (helpNeeded.equals("guides")) {
            for(var listItem: guidesList) {
                int lastIndex = guidesList.get(counter-1).length();
                String fileTitle = guidesList.get(counter-1).substring(0, lastIndex-4);
                outTxt += "/" + counter + " - " + fileTitle + "\n";
                counter++;
            }
        } else if (helpNeeded.equals("steps")) {
            for(var listItem: stepsList) {
                int lastIndex = stepsList.get(counter-1).length();
                String fileTitle = stepsList.get(counter-1).substring(0, lastIndex-4);
                outTxt += "/"+ counter + " - " + fileTitle + "\n";
                counter++;
            }
        }

        outTxt += "Tap the /<bullet no.> to get the file.";
        showText(chatID, outTxt);
    }

    private void showHelpText(String chatID) {
        String outText = "I'm not sure what you're saying. /help";
        showText(chatID, outText);
    }

    private void showContinueText(String chatID) {
        String outText = "Tap /help to continue.";
        showText(chatID, outText);
    }
}