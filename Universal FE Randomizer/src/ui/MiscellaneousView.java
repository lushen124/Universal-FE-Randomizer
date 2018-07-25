package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class MiscellaneousView extends Composite {
	
	private Group container;
	
	private Button randomizeChestVillageRewards;
	private Button randomizeRecruitmentOrder;
	
	public MiscellaneousView(Composite parent, int style) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		
		container.setText("Miscellaneous");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		//////////////////////////////////////////////////////////////////
		
		randomizeChestVillageRewards = new Button(container, SWT.CHECK);
		randomizeChestVillageRewards.setText("Randomize Rewards");
		randomizeChestVillageRewards.setToolTipText("Rewards from chests, villages, and story events will now give out random rewards. Plot-important promotion items are excluded.");
		randomizeChestVillageRewards.setEnabled(false);
		
		FormData chestVillageData = new FormData();
		chestVillageData.left = new FormAttachment(0, 5);
		chestVillageData.top = new FormAttachment(0, 5);
		randomizeChestVillageRewards.setLayoutData(chestVillageData);

		//////////////////////////////////////////////////////////////////

		randomizeRecruitmentOrder = new Button(container, SWT.CHECK);
		randomizeRecruitmentOrder.setText("Randomize Recruitment Order");
		randomizeRecruitmentOrder.setToolTipText("Mixes up the order in which characters join the party.");
		randomizeRecruitmentOrder.setEnabled(false);
		
		FormData randomRecruitData = new FormData();
		randomRecruitData.left = new FormAttachment(0, 5);
		randomRecruitData.top = new FormAttachment(randomizeChestVillageRewards, 5);
		randomizeRecruitmentOrder.setLayoutData(randomRecruitData);
	}

}
