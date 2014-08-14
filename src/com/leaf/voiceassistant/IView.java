
package com.leaf.voiceassistant;

public interface IView {
    public void showProgress(DialogParams params);

    public void talk(VAListItem item);

    public void showDialog(DialogParams params);
    
    public void hideDialog();

	void listenDone();
}
