package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import fedata.general.FEBase.GameType;
import ui.model.ItemAssignmentOptions;
import ui.model.ItemAssignmentOptions.ShopAdjustment;
import ui.model.ItemAssignmentOptions.WeaponReplacementPolicy;

public class ItemAssignmentView extends Composite {

	private Group container;

	//private Group weaponContainer;
	private Button strictWeaponButton;
	private Button rankWeaponButton;
	private Button randomWeaponButton;
	
	/*private Group shopContainer;
	private Button noChangeButton;
	private Button partyAdjustButton;
	private Button randomShopButton;*/
	
	private Button promoWeaponsButton;
	private Button poisonWeaponsButton;
	
	public ItemAssignmentView(Composite parent, int style, GameType type) {
		super(parent, style);
		
		FillLayout layout = new FillLayout();
		setLayout(layout);
		
		container = new Group(this, SWT.NONE);
		//container.setText("Item Assignment");
		//container.setToolTipText("Settings for assigning items.\nHas no effect if classes are not modified (via class randomization or recruitment randomization)");
		container.setText("Weapon Assignment");
		container.setToolTipText("Settings for assigning weapons.\nHas no effect if classes are not modified (via class randomization or recruitment randomization)");
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginLeft = 5;
		mainLayout.marginTop = 5;
		mainLayout.marginBottom = 5;
		mainLayout.marginRight = 5;
		container.setLayout(mainLayout);
		
		/*weaponContainer = new Group(container, SWT.NONE);
		weaponContainer.setText("Weapons");
		
		FormData containerData = new FormData();
		containerData.left = new FormAttachment(0, 0);
		containerData.top = new FormAttachment(0, 0);
		containerData.right = new FormAttachment(100, 0);
		weaponContainer.setLayoutData(containerData);
		
		FormLayout containerLayout = new FormLayout();
		containerLayout.marginLeft = 5;
		containerLayout.marginTop = 5;
		containerLayout.marginBottom = 5;
		containerLayout.marginRight = 5;
		weaponContainer.setLayout(containerLayout);*/
		
		//strictWeaponButton = new Button(weaponContainer, SWT.RADIO);
		strictWeaponButton = new Button(container, SWT.RADIO);
		strictWeaponButton.setText("Strict Matching");
		strictWeaponButton.setToolTipText("Uses the closest analogue to the original weapon as possible.");
		strictWeaponButton.setEnabled(true);
		strictWeaponButton.setSelection(true);
		
		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		strictWeaponButton.setLayoutData(optionData);
		
		//rankWeaponButton = new Button(weaponContainer, SWT.RADIO);
		rankWeaponButton = new Button(container, SWT.RADIO);
		rankWeaponButton.setText("Match Rank");
		rankWeaponButton.setToolTipText("Uses any weapon that matches the rank of the original weapon. Adjusts downwards if no exact matches exist.");
		rankWeaponButton.setEnabled(true);
		rankWeaponButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(strictWeaponButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(strictWeaponButton, 5);
		rankWeaponButton.setLayoutData(optionData);
		
		//randomWeaponButton = new Button(weaponContainer, SWT.RADIO);
		randomWeaponButton = new Button(container, SWT.RADIO);
		randomWeaponButton.setText("Random");
		randomWeaponButton.setToolTipText("Uses any weapon that is usable by the character's weapon ranks.");
		randomWeaponButton.setEnabled(true);
		randomWeaponButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(rankWeaponButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(rankWeaponButton, 5);
		randomWeaponButton.setLayoutData(optionData);
		
		Control lastElement = randomWeaponButton;
		
		if (type.isGBA() && type != GameType.FE6) {
			promoWeaponsButton = new Button(container, SWT.CHECK);
			promoWeaponsButton.setText("Assign Promotional Weapons");
			switch (type) {
			case FE7:
				promoWeaponsButton.setToolTipText("Allows the assignment of Emblem weapons.");
				break;
			case FE8:
				promoWeaponsButton.setToolTipText("Allows the assignment of monster slaying weapons.");
				break;
			default:
				break;
			}
			promoWeaponsButton.setEnabled(true);
			promoWeaponsButton.setSelection(false);
			
			optionData = new FormData();
			optionData.left = new FormAttachment(lastElement, 0, SWT.LEFT);
			optionData.top = new FormAttachment(lastElement, 10);
			promoWeaponsButton.setLayoutData(optionData);
			
			lastElement = promoWeaponsButton;
		}
		
		poisonWeaponsButton = new Button(container, SWT.CHECK);
		poisonWeaponsButton.setText("Assign Poison Weapons");
		poisonWeaponsButton.setToolTipText("Allows the assignment of poison weapons.\nRegardless of this option, enemies may still have them if they had them before.");
		poisonWeaponsButton.setEnabled(true);
		poisonWeaponsButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(lastElement, 0, SWT.LEFT);
		optionData.top = new FormAttachment(lastElement, 10);
		poisonWeaponsButton.setLayoutData(optionData);
		
		lastElement = poisonWeaponsButton;
		
		/*shopContainer = new Group(container, SWT.NONE);
		shopContainer.setText("Shops");
		
		containerData = new FormData();
		containerData.left = new FormAttachment(weaponContainer, 0, SWT.LEFT);
		containerData.right = new FormAttachment(weaponContainer, 0, SWT.RIGHT);
		containerData.top = new FormAttachment(weaponContainer, 10);
		shopContainer.setLayoutData(containerData);
		shopContainer.setLayout(containerLayout);
		
		
		noChangeButton = new Button(shopContainer, SWT.RADIO);
		noChangeButton.setText("No Change");
		noChangeButton.setToolTipText("Do not change shop items.");
		noChangeButton.setEnabled(true);
		noChangeButton.setSelection(true);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		noChangeButton.setLayoutData(optionData);
		
		partyAdjustButton = new Button(shopContainer, SWT.RADIO);
		partyAdjustButton.setText("Adjust to Party");
		partyAdjustButton.setToolTipText("Change shop items to match classes in your party.\nMagic/Staves remain restricted to Item Shops and Physical Weapons remain restricted to Weapon Shops.\nApplies the same logic as weapon assignment above.");
		partyAdjustButton.setEnabled(true);
		partyAdjustButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(noChangeButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(noChangeButton, 5);
		partyAdjustButton.setLayoutData(optionData);
		
		randomShopButton = new Button(shopContainer, SWT.RADIO);
		randomShopButton.setText("Random");
		randomShopButton.setToolTipText("Randomizes shop contents.\nMagic/Staves remain restricted to Item Shops and Physical Weapons remain restricted to Weapon Shops.\nApplies the same logic as weapon assignment above.");
		randomShopButton.setEnabled(true);
		randomShopButton.setSelection(false);
		
		optionData = new FormData();
		optionData.left = new FormAttachment(partyAdjustButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(partyAdjustButton, 5);
		randomShopButton.setLayoutData(optionData);*/
	}
	
	public ItemAssignmentOptions getAssignmentOptions() {
		WeaponReplacementPolicy weaponPolicy = null;
		if (strictWeaponButton.getSelection()) { weaponPolicy = WeaponReplacementPolicy.STRICT; }
		else if (rankWeaponButton.getSelection()) { weaponPolicy = WeaponReplacementPolicy.EQUAL_RANK; }
		else if (randomWeaponButton.getSelection()) { weaponPolicy = WeaponReplacementPolicy.ANY_USABLE; }
		else {
			assert false : "No Weapon Policy Found.";
			weaponPolicy = WeaponReplacementPolicy.STRICT;
		}
		
		ShopAdjustment shopPolicy = null;
		/*if (noChangeButton.getSelection()) { shopPolicy = ShopAdjustment.NO_CHANGE; }
		else if (partyAdjustButton.getSelection()) { shopPolicy = ShopAdjustment.ADJUST_TO_PARTY; }
		else if (randomShopButton.getSelection()) { shopPolicy = ShopAdjustment.RANDOM; }
		else {
			assert false : "No Shop Policy Found.";*/
			shopPolicy = ShopAdjustment.NO_CHANGE;
		//}
		
		return new ItemAssignmentOptions(weaponPolicy, shopPolicy, promoWeaponsButton != null ? promoWeaponsButton.getSelection() : false, poisonWeaponsButton.getSelection());		
	}
	
	public void setItemAssignmentOptions(ItemAssignmentOptions options) {
		if (options == null) { return; }
		
		strictWeaponButton.setSelection(options.weaponPolicy == WeaponReplacementPolicy.STRICT);
		rankWeaponButton.setSelection(options.weaponPolicy == WeaponReplacementPolicy.EQUAL_RANK);
		randomWeaponButton.setSelection(options.weaponPolicy == WeaponReplacementPolicy.ANY_USABLE);
		
		/*noChangeButton.setSelection(options.shopAdjustment == ShopAdjustment.NO_CHANGE);
		partyAdjustButton.setSelection(options.shopAdjustment == ShopAdjustment.ADJUST_TO_PARTY);
		randomShopButton.setSelection(options.shopAdjustment == ShopAdjustment.RANDOM);*/
		
		if (promoWeaponsButton != null) {
			promoWeaponsButton.setSelection(options.assignPromoWeapons);
		}
		poisonWeaponsButton.setSelection(options.assignPoisonWeapons);
	}
}
