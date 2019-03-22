/**
 *
 */
package org.prelle.splittermond.chargen.fluent;

import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.prelle.javafx.AlertType;
import org.prelle.javafx.CloseType;
import org.prelle.javafx.ManagedScreen;
import org.prelle.javafx.ResponsiveControl;
import org.prelle.javafx.ScreenManager;
import org.prelle.javafx.ScreenManagerProvider;
import org.prelle.javafx.WindowMode;
import org.prelle.javafx.fluent.CloseableContent;
import org.prelle.splimo.EquipmentTools;
import org.prelle.splimo.Skill.SkillType;
import org.prelle.splimo.SpliMoCharacter;
import org.prelle.splimo.SplitterMondCore;
import org.prelle.splimo.charctrl.CharacterController;
import org.prelle.splimo.chargen.SpliMoCharacterGenerator;
import org.prelle.splimo.chargen.event.GenerationEvent;
import org.prelle.splimo.chargen.event.GenerationEventDispatcher;
import org.prelle.splimo.chargen.event.GenerationEventListener;
import org.prelle.splimo.chargen.event.GenerationEventType;
import org.prelle.splimo.levelling.CharacterLeveller;
import org.prelle.splittermond.chargen.jfx.CharGenWizardSpliMo;
import org.prelle.splittermond.chargen.jfx.DevelopmentScreenSpliMo;
import org.prelle.splittermond.chargen.jfx.GeneratorRulePlugin;
import org.prelle.splittermond.chargen.jfx.SpliMoCharGenJFXConstants;
import org.prelle.splittermond.chargen.jfx.ViewMode;
import org.prelle.splittermond.jfx.attributes.AttributeScreen;
import org.prelle.splittermond.jfx.creatures.CreatureScreen;
import org.prelle.splittermond.jfx.cultures.CultureLoreScreen;
import org.prelle.splittermond.jfx.equip.EquipmentScreen;
import org.prelle.splittermond.jfx.notes.NotesScreen;
import org.prelle.splittermond.jfx.powers.PowerScreen;
import org.prelle.splittermond.jfx.resources.ResourceScreen;
import org.prelle.splittermond.jfx.skills.SkillScreen2;
import org.prelle.splittermond.jfx.spells.SpellScreen;

import de.rpgframework.RPGFrameworkLoader;
import de.rpgframework.character.Attachment;
import de.rpgframework.character.CharacterHandle;
import de.rpgframework.character.CharacterHandle.Format;
import de.rpgframework.character.CharacterHandle.Type;
import de.rpgframework.character.CharacterProvider;
import de.rpgframework.core.BabylonEventBus;
import de.rpgframework.core.BabylonEventType;
import de.rpgframework.core.CommandBus;
import de.rpgframework.core.CommandResult;
import de.rpgframework.core.CommandType;
import de.rpgframework.core.RoleplayingSystem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

/**
 * @author prelle
 *
 */
public class SplittermondCharGenView extends ManagedScreen implements GenerationEventListener, ScreenManagerProvider, ResponsiveControl, CloseableContent {

	private static Logger logger = LogManager.getLogger(SpliMoCharGenJFXConstants.BASE_LOGGER_NAME);

	private static PropertyResourceBundle uiResources = SpliMoCharGenJFXConstants.UI;

	private static Preferences CONFIG = Preferences.userRoot().node("/org/rpgframework/genesis/splittermond");

	private SpliMoCharacter model;
	private CharacterHandle handle;
	private CharacterController control;
	private ViewMode mode;

	private SplittermondCharDocument scrOverview;
	private AttributeScreen scrAttributes;
	private PowerScreen scrPower;
	private CultureLoreScreen scrCultLore;
	private SkillScreen2 scrSkillNormal;
	private SkillScreen2 scrSkillCombat;
	private SkillScreen2 scrSkillMagic;
	private SpellScreen  scrSpells;
	private ResourceScreen scrResources;
	private EquipmentScreen scrEquipment;
	private CreatureScreen scrCompanions;
	private DevelopmentScreenSpliMo scrDevelopment;
	private NotesScreen scrNotes;

	private MenuItem menuOverview;
	private MenuItem menuAttrib;
	private MenuItem menuStrengthWeakness;
	private MenuItem menuCultLang;
	private MenuItem menuSkillNormal;
	private MenuItem menuSkillCombat;
	private MenuItem menuSkillMagic;
	private MenuItem menuSpells;
	private MenuItem menuResources;
	private MenuItem menuEquipment;
	private MenuItem menuCompanions;
	private MenuItem menuDevelopment;
	private MenuItem menuNotes;
//	private MenuItem menuCommonCharInfo;
//	private MenuItem menuBackgroundInfo;
//	private MenuItem menuSessionReports;

	private transient CharGenWizardSpliMo    wizard;

	//-------------------------------------------------------------------
	public SplittermondCharGenView(CharacterController control, ScreenManager provider, CharacterHandle handle) {
		if (provider==null) throw new RuntimeException("N1");
		if (provider.getScreenManager()==null) throw new RuntimeException("N2");
		this.control = control;
		this.manager = provider;
		this.handle  = handle;
		initComponents();
		initNavigation();

		setTitle(control.getModel().getName());
		GenerationEventDispatcher.addListener(this);
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		scrOverview = new SplittermondCharDocument(control, handle, this);
		scrAttributes = new AttributeScreen(control, ViewMode.MODIFICATION);
		scrPower      = new PowerScreen(control);
		scrCultLore   = new CultureLoreScreen(control);
		scrSkillNormal= new SkillScreen2(control, manager, SkillType.NORMAL);
		scrSkillCombat= new SkillScreen2(control, manager, SkillType.COMBAT);
		scrSkillMagic = new SkillScreen2(control, manager, SkillType.MAGIC);
		scrSpells     = new SpellScreen(control);
		scrResources  = new ResourceScreen(control, manager);
		scrEquipment  = new EquipmentScreen(control, mode, manager);
		scrCompanions = new CreatureScreen(control);
		scrDevelopment= new DevelopmentScreenSpliMo(control);
		scrNotes      = new NotesScreen(control);

		setContent(scrOverview.getContent());
		setTitle(scrOverview.getTitle());
	}

	//-------------------------------------------------------------------
	private void initNavigation() {
		SVGPath svgAfrica = new SVGPath();
		svgAfrica.setContent("M 19.939453 2.9980469 C 19.747553 2.9816381 19.6 3 19.5 3 L 19.464844 3 L 19.429688 3.0019531 C 18.026311 3.1021942 16.639404 3.2032003 15.205078 3.6445312 L 15.177734 3.6523438 L 15.148438 3.6640625 C 14.206682 4.0172277 13.3869 4.3782928 13.044922 4.3164062 C 13.005733 4.2975902 12.97417 4.2820067 12.847656 4.21875 C 12.674953 4.1323986 12.372222 4 12 4 C 11.738889 4 11.477535 4.03672 11.203125 4.1464844 C 10.928715 4.2562484 10.605321 4.4553732 10.40625 4.8535156 C 10.269992 5.126032 10.252007 5.508203 10.279297 5.6953125 C 10.285097 5.6482415 10.294957 5.6698245 10.011719 5.8691406 C 9.6972271 6.0904494 9.0761151 6.3908237 8.8378906 7.2246094 L 8.8339844 7.2421875 L 8.8300781 7.2578125 C 8.6483831 7.9845925 8.7419779 8.4547707 8.7324219 8.5292969 C 8.6784 8.7081361 8.3824953 8.8837841 7.7851562 9.2421875 L 7.765625 9.2539062 L 7.7460938 9.2675781 C 6.7465317 9.9339521 5.8881674 10.786165 5.3046875 11.953125 C 5.2046875 12.153125 5 12.466667 5 13 C 5 13.7 5.3712092 14.031095 5.59375 14.269531 C 5.8162908 14.507968 6.0109808 14.717643 5.96875 14.654297 L 5.9921875 14.691406 L 6.0195312 14.724609 C 6.0843292 14.805609 6.1090079 15.110475 5.9511719 15.583984 C 5.8297367 15.94829 5.4813309 16.402614 5.2011719 17.070312 C 4.9943994 17.4995 5 17.901858 5 18 L 5 18.414062 L 5.6367188 19.050781 L 5.6855469 19.089844 C 6.5572112 19.767805 7.3055404 20.424803 7.9472656 21.158203 L 7.9199219 21.125 C 8.4316559 21.764668 8.95202 21.998165 9.3847656 22.257812 C 9.9352064 22.588078 10.667308 23.08646 11.695312 22.880859 C 12.133887 22.793149 12.435435 22.626515 12.660156 22.525391 C 12.884878 22.424266 13.016667 22.400391 13 22.400391 C 12.92778 22.400391 13.04961 22.410938 13.224609 22.460938 C 13.374925 22.503888 13.604397 22.560808 13.890625 22.580078 C 14.988383 22.735422 15.749357 22.15846 16.072266 22.029297 C 16.044786 22.040287 16.194008 22.008595 16.353516 22.046875 C 16.513023 22.085155 16.670136 22.183808 16.693359 22.207031 L 16.763672 22.277344 L 16.845703 22.332031 C 16.782353 22.289801 16.817969 22.305081 16.917969 22.455078 C 17.013929 22.599016 17.18612 22.86998 17.519531 23.105469 C 18.014074 23.46747 18.534277 23.492293 19.019531 23.414062 C 19.038831 23.444703 19.04588 23.442759 19.0625 23.474609 C 19.164846 23.670772 19.199219 23.934115 19.199219 23.800781 C 19.199219 23.834111 19.184602 23.914287 19.138672 24.113281 C 19.099562 24.282762 19.039505 24.579741 19.109375 24.931641 C 19.213084 26.040645 19.417133 26.993066 20.210938 28.013672 C 20.814526 28.789716 21.081789 29.406797 21.091797 30.353516 C 21.053427 30.722863 20.880632 31.156365 20.644531 31.703125 C 20.401969 32.264849 20.099609 32.950781 20.099609 33.800781 C 20.099609 34.835397 20.648262 35.588183 21.082031 36.179688 C 21.515801 36.771191 21.858947 37.260009 21.929688 37.542969 C 21.981427 37.749924 21.985258 38.244362 22.126953 38.824219 C 22.361609 39.87354 22.821313 40.78246 23.351562 41.630859 L 23.359375 41.642578 L 23.367188 41.654297 C 23.687794 42.135206 23.782751 42.18099 23.800781 42.205078 L 23.800781 42.599609 C 23.800781 43.632943 24.465272 44.383374 25.328125 44.728516 L 25.355469 44.740234 L 25.382812 44.748047 C 26.11067 44.990666 26.613175 44.774585 27.009766 44.671875 C 27.613464 44.611215 28.378727 44.494907 29.162109 44.238281 C 29.988816 43.967464 30.849261 43.588667 31.419922 42.773438 L 31.478516 42.689453 L 31.519531 42.59375 C 31.787837 41.967704 32.149234 41.366391 32.507812 41.007812 C 32.657814 40.857813 32.860807 40.670036 33.050781 40.361328 C 33.161676 40.181124 33.140141 39.872075 33.177734 39.599609 L 33.398438 39.599609 L 33.496094 39.580078 C 34.098097 39.459677 34.529146 39.015702 34.730469 38.587891 C 34.928252 38.167601 34.977731 37.73835 34.869141 37.28125 C 34.758752 36.773787 34.488965 36.433534 34.314453 36.21875 C 34.18092 36.054402 34.142687 35.990936 34.101562 35.925781 C 34.121542 35.890331 34.122867 35.868734 34.154297 35.833984 C 34.321042 35.649687 34.575556 35.442411 34.708984 35.361328 C 35.275462 35.086176 36.192352 34.753967 36.695312 33.748047 L 36.712891 33.710938 L 36.728516 33.671875 C 37.013893 32.958432 37.013893 32.141177 36.728516 31.427734 L 36.78125 31.603516 C 36.553593 30.465229 36.175343 29.597176 36.095703 28.800781 L 36.089844 28.751953 L 36.080078 28.703125 C 35.982578 28.215638 36.362235 27.428644 36.859375 26.529297 C 37.148278 26.108634 37.629406 25.681958 38.154297 25.332031 L 38.177734 25.316406 L 38.199219 25.300781 C 39.237619 24.521981 40.633476 23.369236 41.689453 22.066406 C 42.217442 21.414991 42.667871 20.726857 42.921875 19.964844 C 43.175879 19.202831 43.206097 18.300681 42.775391 17.517578 L 42.183594 16.439453 L 41.25 17.240234 C 40.814178 17.613796 39.726895 18.01717 38.912109 18.101562 C 38.782606 18.089323 38.643574 18.067761 38.619141 18.078125 C 38.599367 18.058694 38.584477 18.034188 38.59375 18.052734 L 38.539062 17.943359 L 38.458984 17.849609 C 38.05805 17.381855 37.759658 16.306827 36.75 15.441406 L 36.726562 15.419922 L 36.699219 15.400391 C 36.213885 15.036391 35.940781 14.885885 35.800781 14.699219 L 35.757812 14.642578 L 35.707031 14.59375 C 35.542734 14.429452 35.348437 13.982813 35.148438 13.382812 L 35.146484 13.375 L 35.142578 13.367188 C 34.599965 11.829643 34.597815 10.398266 33.824219 8.5195312 L 33.810547 8.4863281 L 33.794922 8.453125 C 33.709832 8.282939 33.60269 8.0183758 33.388672 7.71875 C 33.174653 7.4191242 32.716276 7 32.099609 7 L 31.328125 7 L 31.257812 7.0097656 C 30.831696 7.0706346 30.337917 6.948144 29.712891 6.7050781 C 29.10718 6.4695241 28.403576 6.1340282 27.578125 5.9453125 C 27.313157 5.861442 27.041045 5.816404 26.736328 5.8398438 C 26.417427 5.8643747 26.029062 5.9572664 25.693359 6.2929688 C 25.349461 6.6368672 25.289135 6.9775574 25.236328 7.1972656 C 25.175508 7.1612496 25.075897 7.0838552 24.773438 6.8808594 C 24.071836 6.3897389 23.265625 6.1503906 22.515625 5.9003906 C 22.0852 5.7569155 21.845073 5.6272591 21.515625 5.4882812 C 21.522225 5.4683373 21.542478 5.4350906 21.548828 5.4160156 C 21.714778 4.9181651 21.616126 4.5354311 21.478516 4.1914062 C 21.340906 3.8473814 21.138587 3.524032 20.753906 3.2675781 L 20.703125 3.234375 L 20.646484 3.2050781 C 20.367598 3.0656356 20.131353 3.0144557 19.939453 2.9980469 z M 19.558594 5 C 19.499504 5.1599931 19.444363 5.3295486 19.367188 5.4453125 L 18.826172 6.2578125 L 19.626953 6.8183594 C 20.328554 7.3094799 21.132813 7.5488281 21.882812 7.7988281 C 22.632812 8.0488281 23.328554 8.3106517 23.626953 8.5195312 L 23.636719 8.5253906 L 23.644531 8.53125 C 23.859193 8.6743577 24.130612 8.9641101 24.683594 9.1484375 C 24.960085 9.2406015 25.245552 9.2842964 25.564453 9.2597656 C 25.883354 9.2352346 26.27172 9.1423429 26.607422 8.8066406 C 26.961974 8.4520652 27.028375 8.0911905 27.080078 7.875 L 27.089844 7.8769531 C 27.629262 7.9925427 28.263308 8.2883784 28.988281 8.5703125 C 29.713255 8.8522466 30.56713 9.129361 31.541016 8.9902344 L 31.400391 9 L 31.824219 9 C 31.864009 9.071765 31.909259 9.1567234 31.978516 9.2949219 C 32.59716 10.805204 32.60388 12.1692 33.251953 14.015625 C 33.252342 14.016825 33.253506 14.018331 33.253906 14.019531 C 33.44841 14.603557 33.668644 15.333088 34.265625 15.957031 C 34.691537 16.484561 35.157927 16.751325 35.458984 16.970703 C 35.810081 17.281331 36.164088 18.114546 36.804688 18.947266 C 37.023944 19.38578 37.313439 19.675274 37.751953 19.894531 C 38.309723 20.173421 38.7 20.099609 39 20.099609 L 39.050781 20.099609 L 39.099609 20.095703 C 39.65387 20.040273 40.233158 19.908661 40.792969 19.716797 C 40.625312 20.060299 40.456007 20.412257 40.134766 20.808594 C 39.265742 21.880758 37.9616 22.978019 37 23.699219 C 36.349342 24.13736 35.670325 24.691777 35.167969 25.445312 L 35.144531 25.478516 L 35.125 25.513672 C 34.6405 26.385772 33.837448 27.579648 34.121094 29.068359 C 34.251845 30.232552 34.651893 31.153997 34.820312 31.996094 L 34.837891 32.085938 L 34.871094 32.171875 C 34.978016 32.43918 34.963644 32.621627 34.871094 32.876953 C 34.771524 33.041024 34.372085 33.295403 33.751953 33.605469 L 33.71875 33.623047 L 33.685547 33.642578 C 33.317852 33.863195 32.97853 34.153253 32.671875 34.492188 C 32.365219 34.831121 32 35.117057 32 35.900391 C 32 35.980692 32.044143 36.359849 32.232422 36.681641 C 32.408375 37.059165 32.62945 37.317677 32.761719 37.480469 C 32.843109 37.58064 32.829819 37.567429 32.849609 37.599609 L 32.699219 37.599609 C 32.432552 37.599609 32.227535 37.636329 31.953125 37.746094 C 31.81592 37.800974 31.663503 37.87146 31.488281 38.033203 C 31.31306 38.194946 31.099609 38.517057 31.099609 38.900391 C 31.099609 39.191826 31.189679 39.300327 31.255859 39.414062 C 31.212609 39.464182 31.189559 39.497942 31.09375 39.59375 C 30.493185 40.194315 30.073726 40.931418 29.75 41.658203 C 29.594054 41.846117 29.131952 42.143668 28.539062 42.337891 C 27.915769 42.542073 27.19368 42.661015 26.708984 42.705078 L 26.632812 42.710938 L 26.556641 42.730469 C 26.179566 42.824739 25.95748 42.808199 26.046875 42.845703 C 25.924088 42.766683 25.800781 42.569394 25.800781 42.599609 L 25.800781 41.876953 L 25.769531 41.757812 C 25.577284 40.988822 25.110156 40.647604 25.041016 40.554688 C 24.577858 39.811544 24.240982 39.124459 24.076172 38.382812 L 24.074219 38.371094 L 24.070312 38.357422 C 24.017872 38.147646 24.018652 37.646865 23.871094 37.056641 C 23.641834 36.139601 23.08459 35.52959 22.693359 34.996094 C 22.302129 34.462597 22.099609 34.066166 22.099609 33.800781 C 22.099609 33.450781 22.248031 33.03437 22.480469 32.496094 C 22.712906 31.957818 23.026994 31.306931 23.095703 30.482422 L 23.099609 30.441406 L 23.099609 30.400391 C 23.099609 28.978168 22.574071 27.794451 21.789062 26.785156 C 21.192961 26.018739 21.193229 25.781774 21.095703 24.708984 L 21.085938 24.59375 L 21.082031 24.580078 C 21.083927 24.569543 21.081582 24.581354 21.085938 24.5625 C 21.115018 24.436494 21.199219 24.167448 21.199219 23.800781 C 21.199219 23.267448 21.033591 22.929618 20.835938 22.550781 C 20.638358 22.171909 20.413333 21.81 20 21.5 L 19.505859 21.128906 L 18.953125 21.40625 C 18.73201 21.516807 18.693885 21.496 18.699219 21.5 L 18.677734 21.482422 L 18.654297 21.46875 C 18.717647 21.51098 18.682031 21.4957 18.582031 21.345703 C 18.494751 21.214787 18.309917 20.972329 18.035156 20.753906 C 17.672579 20.416655 17.259617 20.208949 16.820312 20.103516 C 16.35482 19.991797 15.855601 19.960884 15.328125 20.171875 C 14.657371 20.440177 14.230878 20.624417 14.164062 20.613281 L 14.082031 20.599609 L 14 20.599609 C 14.07222 20.599609 13.95039 20.589063 13.775391 20.539062 C 13.600391 20.489062 13.372222 20.400391 13 20.400391 C 12.483333 20.400391 12.115122 20.577297 11.839844 20.701172 C 11.564565 20.825047 11.366113 20.907637 11.304688 20.919922 C 11.332688 20.914322 10.863622 20.812704 10.414062 20.542969 C 9.8468087 20.202616 9.7687347 20.235332 9.4804688 19.875 L 9.4667969 19.857422 L 9.453125 19.841797 C 8.7553613 19.044359 7.9601319 18.363305 7.1171875 17.695312 C 7.2751302 17.389533 7.6049626 16.950737 7.8496094 16.216797 C 8.0901589 15.495148 8.3035624 14.40933 7.5859375 13.494141 C 7.3694446 13.192611 7.1758848 13.031447 7.0585938 12.90625 C 7.0721787 12.86897 7.055554 12.924046 7.09375 12.847656 C 7.5098285 12.015499 8.0544704 11.466685 8.8535156 10.933594 C 8.8535156 10.933594 8.8554688 10.931641 8.8554688 10.931641 C 9.2751268 10.68926 10.321138 10.267619 10.662109 9.0742188 L 10.666016 9.0585938 L 10.669922 9.0429688 C 10.841415 8.3569972 10.770311 7.9264045 10.769531 7.8144531 C 10.795653 7.8102151 10.901619 7.6891663 11.162109 7.5058594 C 11.487988 7.2765375 12.015576 6.8424837 12.203125 6.1210938 C 12.293745 6.1668908 12.338042 6.1992698 12.484375 6.2480469 L 12.533203 6.265625 L 12.583984 6.2753906 C 14.02888 6.5965146 15.161173 5.8099481 15.8125 5.5546875 C 16.969603 5.2011686 18.173905 5.0989897 19.558594 5 z M 41.699219 30 C 41.399219 30.6 41 31.299609 40.5 31.599609 C 40.1 31.799609 39.600391 32.000391 39.400391 32.400391 C 39.100391 32.900391 39.400781 33.5 39.300781 34 C 39.300781 34.2 39.199609 34.399609 39.099609 34.599609 C 38.999609 35.099609 39.100781 35.699219 39.300781 36.199219 C 39.400781 36.499219 39.600391 36.9 39.900391 37 C 40.100391 37 40.399609 36.899219 40.599609 36.699219 C 40.799609 36.499219 40.999609 36.100781 41.099609 35.800781 C 41.199609 35.500781 41.199219 35.200391 41.199219 34.900391 C 41.299219 34.000391 41.8 33.099219 42 32.199219 C 42.1 31.599219 42.1 31.000391 42 30.400391 C 41.9 30.200391 41.799219 30 41.699219 30 z M 32.925781 37.732422 C 32.925781 37.732422 32.927734 37.732422 32.927734 37.732422 L 32.929688 37.742188 C 32.926757 37.730469 32.927478 37.737125 32.925781 37.732422 z M 32.699219 39.599609 L 32.703125 39.599609 C 32.702546 39.599819 32.697766 39.603316 32.697266 39.603516 C 32.672856 39.613316 32.765839 39.599609 32.699219 39.599609 z");
		Region icoCultLang = new Region();
		icoCultLang.setShape(svgAfrica);
		icoCultLang.setMinSize(15,15);
		icoCultLang.setPrefSize(20,20);
		icoCultLang.setStyle("-fx-background-color: black");
		icoCultLang.setScaleX(0.5);

		SVGPath svgCombat = new SVGPath();
		svgCombat.setContent("M491.844 22.533l-83.42 14.865L196.572 249.25c3.262 4.815 5.37 10.72 5.37 16.932 0 5.863-1.71 11.35-4.643 15.996-5.065-1.606-10.448-2.477-16.027-2.477-15.724 0-29.904 6.89-39.69 17.796l-9.112-9.113 17.237-17.237c-4.515-5.772-8.907-11.645-13.19-17.6l-19.443 19.44-13.215-13.215 21.828-21.827c-4.403-6.59-8.67-13.278-12.792-20.068l-40.802 40.803 58.314 58.314c-1.613 5.075-2.49 10.47-2.49 16.063 0 7.666 1.65 14.96 4.592 21.564l-72.14 72.14-14.56-14.56L21.013 437l14.558 14.56-8.607 8.608 27.246 27.246 8.606-8.61 14.56 14.56 24.798-24.8-14.557-14.556 72.158-72.16c6.586 2.922 13.858 4.562 21.498 4.562 5.593 0 10.988-.877 16.063-2.49l58.363 58.363L296.5 401.48c-6.797-4.127-13.486-8.395-20.068-12.793l-21.83 21.83L241.39 397.3l19.442-19.44c-5.962-4.29-11.835-8.683-17.603-13.194l-17.238 17.238-9.16-9.16c10.905-9.785 17.795-23.965 17.795-39.69 0-5.346-.806-10.51-2.285-15.39 4.703-3.04 10.288-4.817 16.265-4.816 6.21 0 11.776 1.77 16.52 4.955L476.98 105.95l14.864-83.417zm-66.227 53.012l13.215 13.215-191.684 191.68-13.214-13.213L425.617 75.545zM181.273 298.39c19.257 0 34.665 15.41 34.665 34.665 0 19.256-15.408 34.666-34.665 34.666-19.256 0-34.666-15.41-34.666-34.665s15.41-34.666 34.666-34.666z");
		Region icoCombat = new Region();
		icoCombat.setShape(svgCombat);
		icoCombat.setMinSize(15,15);
		icoCombat.setPrefSize(20,20);
		icoCombat.setStyle("-fx-background-color: black");
		icoCombat.setScaleX(0.5);

		SVGPath svgMagic = new SVGPath();
		svgMagic.setContent("M250.53 22.03c-57.055 45.157-80.673 37.81-100.31.22 16.598 61.517 10.408 66.415-44.72 116.594 67.324-35.666 96.206-34.238 130.97 7.187-34.906-53.112-30.954-75.35 14.06-124zm18.407.126l11.688 114.938-99.875 58.094 97.75 21.093c-9.58 8.352-20.214 19.028-31.28 30.095l-.032.03L18.563 472.438v19.438h32.156L273.343 272.5c10.26-10.263 18.902-19.538 25.78-27.75l18.938 87.75 58.094-99.875 114.938 11.688-77.03-86.094 46.655-105.69-105.69 46.657-86.092-77.03zM26.875 55.938c33.765 27.66 35.21 42.767 30.75 87.78 18.975-53.73 27.964-67.297 64.5-82C82.972 71.094 66.21 73 26.875 55.94zm54.75 102.406c24.955 27.012 26.97 43.684 24.25 72.062 14.775-34.45 22.072-45.66 55.625-64.312-34.56 11.183-45.5 10.22-79.875-7.75zm325.594 95c9.27 51.694-4.61 73.708-32.845 106.687 43.3-37.043 57.852-44.284 96.844-38.75-38.597-11.457-47.426-20.624-64-67.936zm-55.658 72.812c-18.705 68.79-45.304 83.944-107.625 70.125 54.126 20.1 56.34 21.07 53.532 85.25 24.757-55.42 46.49-52.217 95.06-37.217-41.775-31.838-45.71-48.97-40.967-118.157zm109.344 55.97c-15.32 17.994-22.932 17.49-43.812 9.343 22.828 18.444 17.596 34.024 10.844 59.405 16.05-19.12 23.516-25.237 50.312-12.688-22.86-21.342-27.13-29.857-17.344-56.062z");
		Region icoMagic = new Region();
		icoMagic.setShape(svgMagic);
		icoMagic.setMinSize(15,15);
		icoMagic.setPrefSize(20,20);
		icoMagic.setStyle("-fx-background-color: black");
		icoMagic.setScaleX(0.5);

		SVGPath svgSpells = new SVGPath();
		svgSpells.setContent("M485.846 30l-172.967 74.424 64.283 20.32-129.627 65.186 83.637 19.414-96.996 62.219 219.133-69.7-95.29-28.326L471.192 112.8l-72.115-15.024L485.846 30zm-280.46 45.766c-28.066-.117-49.926 56.532-57.726 90.607-11.26 49.19-14.529 83.515-.828 133.059l-17.348 4.798c-15.463-55.917-8.245-94.75 2.301-142.341 10.547-47.592 14.52-70.403-4.459-74.182C85.244 79.328 82.04 178.17 79.57 222.604c-1.396 25.808.71 57.017 6.54 77.552l-16.901 6.196c-14.43-53.35-6.657-97.957-1.693-150.77 2.493-15.582-1.787-25.677-19.102-25.166-15.833.467-27.015 143.362-13.275 179.041 8.713 53.061 31.247 130.572 10.955 152.766L18 494h205.973l19.986-28.592c23.08-5.008 28.42-19.86 37.023-33.787 25.291-40.946 82.384-83.166 129.114-99.226 21.142-7.51-21.912-48.546-53.836-32.782-55.005 27.162-81.646 56.298-117.772 38.295-55.855-27.834-47.245-100.648-35.861-162.83 6.141-33.544 40.41-89.602 7.156-98.824a21.158 21.158 0 0 0-4.396-.488z");
		Region icoSpells = new Region();
		icoSpells.setShape(svgSpells);
		icoSpells.setMinSize(15,15);
		icoSpells.setPrefSize(25,25);
		icoSpells.setStyle("-fx-background-color: black");
		icoSpells.setScaleX(0.5);

		SVGPath svgBattleGear = new SVGPath();
		svgBattleGear.setContent("M262.406 17.188c-27.22 8.822-54.017 28.012-72.375 55.53 17.544 47.898 17.544 57.26 0 105.157 19.92 15.463 40.304 24.76 60.782 27.47-2.063-25.563-3.63-51.13 1.125-76.69-13.625-1.483-23.374-5.995-37-13.874V82.563c35.866 19.096 61.84 18.777 98.813 0v32.22c-13.364 6.497-21.886 11.16-35.25 13.218 3.614 25.568 3.48 51.15 1.375 76.72 18.644-3.265 37.236-12.113 55.5-26.845-14.353-47.897-14.355-57.26 0-105.156-16.982-28.008-47.453-46.633-72.97-55.532zm-129.594 8.218c-25.906 110.414-27.35 215.33-27.4 330.922-18.84-1.537-37.582-5.12-56.027-11.12v28.554h69.066c8.715 35.025 6.472 70.052-1.036 105.078h28.13c-7.195-35.026-8.237-70.053-.872-105.078h68.904v-28.555c-18.49 4.942-37.256 8.552-56.097 10.46.082-114.94 2.496-223.068-24.667-330.26zm89.47 202.375c0 117.27 25.517 233.342 120.155 257.97C446.62 464.716 462.72 345.374 462.72 227.78H222.28z");
		Region icoGear = new Region();
		icoGear.setShape(svgBattleGear);
		icoGear.setMinSize(15,15);
		icoGear.setPrefSize(25,25);
		icoGear.setStyle("-fx-background-color: black");
		icoGear.setScaleX(0.5);

		SVGPath svgChest = new SVGPath();
		svgChest.setContent("M146.857 20.842c-12.535-.036-24.268 2.86-37.285 9.424h.004C61.356 54.6 19.966 120.734 17.982 175.91l41.848 14.236c4.33-61.89 47.057-128.37 101.527-155.86h.002c4.423-2.23 8.822-4.162 13.185-5.8l-22.26-7.45c-1.83-.123-3.637-.19-5.428-.194zm59.34 20.19c-10.478-.09-22.832 3.093-36.424 9.943l.004-.004c-48.23 24.34-89.625 90.513-91.548 145.436l156.485 53.24c3.865-62.22 46.797-129.372 101.613-157.035h.002l.002-.003c4.303-2.168 8.584-4.056 12.832-5.666l-134.54-45.036c-2.652-.542-5.458-.847-8.427-.873zm174.97 58.323c-10.476-.09-22.83 3.092-36.42 9.94l-.005.002c-48.577 24.518-90.225 91.473-91.586 146.623l46.205 15.72c3.914-62.188 46.825-129.274 101.607-156.92 4.522-2.283 9.04-4.258 13.53-5.91l-26.544-8.884c-2.164-.35-4.423-.55-6.785-.57zm63.554 22.014c-10.267.093-22.094 3.353-35.333 10.034-47.158 23.8-87.777 87.587-91.362 141.75l174.55-73.726c-.404-39.01-10.754-61.304-24.415-71.082-2.347-1.68-4.867-3.057-7.55-4.137l-.01.034-4.735-1.584c-3.48-.887-7.195-1.327-11.144-1.29zM17.9 195.622l-.035 187.484L59.46 397.58V209.764L17.9 195.624zM78.15 216.12v187.962l156.282 54.37V269.288l-29.053-9.886v119.43l-101.054-34.082V225.025L78.15 216.12zm414.22 3.683L318.433 293.27v189.236l173.935-73.504v-189.2zm-369.354 11.582v99.947l63.675 21.477v-99.763l-63.674-21.662zm31.306 28.797c9.705 0 17.573 7.867 17.573 17.572 0 6.34-3.37 11.88-8.407 14.97v28.53h-18.69v-28.746c-4.838-3.13-8.048-8.562-8.048-14.754 0-9.705 7.867-17.572 17.572-17.572zm98.797 15.464v189.307l46.626 16.22V291.51l-46.627-15.864z");
		Region icoChest = new Region();
		icoChest.setShape(svgChest);
		icoChest.setMinSize(15,15);
		icoChest.setPrefSize(25,25);
		icoChest.setStyle("-fx-background-color: black");
		icoChest.setScaleX(0.6);

		menuOverview = new MenuItem(uiResources.getString("section.overview"), new Label("\uE80F"));
		menuAttrib   = new MenuItem("Attribute", new Label("\uE779"));
		menuStrengthWeakness = new MenuItem(uiResources.getString("section.powerweak"), new Label("\uD83C\uDFAD"));
		menuCultLang = new MenuItem(uiResources.getString("section.cultlang"), icoCultLang);
		menuSkillNormal = new MenuItem("Allgemeine Fertigkeiten", new Label("\uE7BE"));
		menuSkillCombat = new MenuItem("Kampffertigkeiten", icoCombat);
		menuSkillMagic = new MenuItem("Zauberschulen", icoMagic);
		menuSpells = new MenuItem(uiResources.getString("section.spells"), icoSpells);
		menuResources = new MenuItem(uiResources.getString("section.resources"), icoChest);
		menuEquipment = new MenuItem(uiResources.getString("section.equipment"), icoGear);
		menuCompanions  = new MenuItem(uiResources.getString("section.companions"), new Label("\uD83D\uDC0E"));
		menuDevelopment = new MenuItem(uiResources.getString("section.development"), new Label("\uD83D\uDCC8"));
		menuNotes = new MenuItem(uiResources.getString("section.notes"), new Label("\uE929"));

//		menuCommonCharInfo = new MenuItem("Sonstiges", new Label("\uE723"));
//		menuBackgroundInfo = new MenuItem("Hintergrund", new Label("\uD83D\uDC40"));
//		menuSessionReports = new MenuItem("Spielberichte", new Label("\uE77C"));
		getStaticButtons().addAll(menuOverview, menuAttrib, menuStrengthWeakness, menuCultLang, menuSkillNormal);
		getStaticButtons().add(menuSkillCombat);
		getStaticButtons().add(menuSkillMagic);
		getStaticButtons().add(menuSpells);
		getStaticButtons().add(menuResources);
		getStaticButtons().add(menuEquipment);
		getStaticButtons().add(menuCompanions);
		getStaticButtons().add(menuDevelopment);
		getStaticButtons().add(menuNotes);
//		getStaticButtons().add(new NavigationPane.SpacingMenuItem());
//		getStaticButtons().add(new MenuItem("Drucken", new Label("\uD83D\uDDB6")));
//		getStaticButtons().add(new NavigationPane.SpacingMenuItem());
//		getStaticButtons().add(menuCommonCharInfo);
//		getStaticButtons().add(menuBackgroundInfo);
//		getStaticButtons().add(menuSessionReports);


		menuOverview.setOnAction(ev -> {
			getScreenManager().replaceContent(scrOverview);
		});

		menuAttrib.setOnAction(ev -> {
			scrAttributes.setData(control.getModel());
			getScreenManager().show(scrAttributes);
		});
		menuStrengthWeakness.setOnAction(ev -> {
			scrPower.setData(control.getModel());
			getScreenManager().show(scrPower);
		});
		menuCultLang.setOnAction(ev -> {
			scrCultLore.setData(model);
			getScreenManager().show(scrCultLore);
		});
		menuSkillNormal.setOnAction(ev -> {
			scrSkillNormal.setData(model);
			getScreenManager().show(scrSkillNormal);
		});
		menuSkillCombat.setOnAction(ev -> {
			scrSkillCombat.setData(model);
			getScreenManager().show(scrSkillCombat);
		});
		menuSkillMagic.setOnAction(ev -> {
			scrSkillMagic.setData(model);
			getScreenManager().show(scrSkillMagic);
		});
		menuSpells.setOnAction(ev -> {
			scrSpells.setData(model);
			getScreenManager().show(scrSpells);
		});
		menuResources.setOnAction(ev -> {
			getScreenManager().show(scrResources);
		});
		menuEquipment.setOnAction(ev -> {
			getScreenManager().show(scrEquipment);
		});
		menuCompanions.setOnAction(ev -> {
			scrCompanions.setData(model);
			getScreenManager().show(scrCompanions);
		});
		menuDevelopment.setOnAction(ev -> {
			logger.debug("development");
			getScreenManager().show(scrDevelopment);
		});
		menuNotes.setOnAction(ev -> {
			getScreenManager().show(scrNotes);
		});
	}

	//-------------------------------------------------------------------
	public void setData(SpliMoCharacter model, CharacterHandle handle) {
		logger.debug("Show character "+model.getName());
		logger.debug(model.dump());
		this.model = model;
		this.handle= handle;

		setTitle(control.getModel().getName());
		scrOverview.setData(model, handle);
		scrAttributes.setData(model);
		scrCompanions.setData(model);
		scrDevelopment.setData(model);
		scrNotes.setData(model);
		scrPower.setData(model);
//		scrResources.setData(model);
		scrSkillCombat.setData(model);
		scrSkillMagic.setData(model);
		scrSkillNormal.setData(model);
		scrSpells.setData(model);
	}

	//--------------------------------------------------------------------
	/**
	 * @see org.prelle.splimo.chargen.event.GenerationEventListener#handleGenerationEvent(org.prelle.splimo.chargen.event.GenerationEvent)
	 */
	@Override
	public void handleGenerationEvent(GenerationEvent event) {
		switch (event.getType()) {
		case BASE_DATA_CHANGED:
			logger.debug("RCV "+event.getType());
			setTitle(model.getName());
			break;
		case POINTS_LEFT_ATTRIBUTES:
		case POINTS_LEFT_MASTERSHIPS:
		case POINTS_LEFT_POWERS:
		case POINTS_LEFT_RESOURCES:
//			updateAttentionFlags();
			break;
		case EXPERIENCE_CHANGED:
			logger.debug("RCV "+event);
			scrOverview.refresh();
			break;
		case ATTRIBUTE_CHANGED:
			// update items after attribute change in case min Requirements are now met.
			logger.info("attribute changed, updating items..");
			boolean mightHaveChanged = EquipmentTools.updateAllItems(model);
			if (mightHaveChanged) {
				GenerationEventDispatcher.fireEvent(
						new GenerationEvent(GenerationEventType.ITEM_CHANGED, model)
				);
			}
			break;
		default:
		}

	}

	//-------------------------------------------------------------------
	public void setScreenManager(ScreenManager manager) {
		this.manager = manager;
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ResponsiveControl#setResponsiveMode(org.prelle.javafx.WindowMode)
	 */
	@Override
	public void setResponsiveMode(WindowMode value) {
//		super.setResponsiveMode(value);

	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.fluent.NavigableContentNode#getStyleSheets()
	 */
	@Override
	public String[] getStyleSheets() {
		return new String[] {GeneratorRulePlugin.class.getResource("css/splittermond.css").toExternalForm()};
	}

	//-------------------------------------------------------------------
	public void startGeneration(SpliMoCharacter model) {
		logger.info("startGeneration "+model);
		this.model = model;
		this.mode = ViewMode.GENERATION;

		setData(model, null);
		getStaticButtons().remove(menuDevelopment);
		menuDevelopment.setDisable(true);

		wizard = new CharGenWizardSpliMo(model, (SpliMoCharacterGenerator)control);
		CloseType close = (CloseType)manager.showAndWait(wizard);
		logger.info("Closed with "+close);

		if (close==CloseType.FINISH) {
			logger.info("Wizard finished");
			try {
				byte[] data =SplitterMondCore.save(model);
				handle = RPGFrameworkLoader.getInstance().getCharacterService().createCharacter(model.getName(), RoleplayingSystem.SPLITTERMOND);
				RPGFrameworkLoader.getInstance().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, model.getName()+".xml", data);
				manager.showAlertAndCall(AlertType.NOTIFICATION, uiResources.getString("alert.start_tuning.title"),
						String.format(uiResources.getString("alert.start_tuning.message"), handle.getPath().toString()));
//				commandBar.getItems().addAll(cmdPrint);
			} catch (IOException e) {
				logger.error("Failed writing newly created character to disk",e);
				manager.showAlertAndCall(AlertType.ERROR, uiResources.getString("error.saving_character.title"),
						String.format(uiResources.getString("error.saving_character.message"), e.toString()));
			}
			setData(model, handle);
//			refresh();
		} else {
			logger.warn("Wizard "+close);
//			getScreenManager().closeCurrent(close);
			getScreenManager().cancel();

		}
	}

	//-------------------------------------------------------------------
	private void saveCharacter() {
		logger.debug("START: saveCharacter");

		if (mode==ViewMode.MODIFICATION) {
			/*
			 * Write all made modifications to character
			 */
			logger.debug("Add modifications to character log");
			((CharacterLeveller)control).updateHistory();
		}

		try {
			/*
			 * 1. Call plugin to encode character
			 * 2. Use character service to save character
			 */
			logger.debug("encode character "+model.getName());
			CommandResult result = CommandBus.fireCommand(this, CommandType.ENCODE,
					handle.getRuleIdentifier(),
					model
					);
			if (!result.wasProcessed()) {
				logger.error("Cannot save character, since encoding failed");
				manager.showAlertAndCall(
						AlertType.ERROR,
						"Das hätte nicht passieren dürfen",
						"Es hat sich kein Plugin gefunden, welches das Kodieren von Charakteren dieses Systems erlaubt."
						);
			} else {
				byte[] encoded = (byte[]) result.getReturnValue();
				try {
					logger.info("Save character "+model.getName());
					RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, null, encoded);
					logger.info("Saved character "+model.getName()+" successfully");
				} catch (IOException e) {
					logger.error("Failed saving character",e);
					BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Failed saving character.\n"+e);
				}
			}

			/*
			 * Update portrait
			 */
			logger.debug("Update portrait");
			CharacterProvider charServ = RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService();
			try {
				if (model.getImage()!=null && handle!=null) {
					Attachment attach = handle.getFirstAttachment(Type.CHARACTER, Format.IMAGE);
					if (attach!=null) {
						logger.info("Update character image");
						attach.setData(model.getImage());
						charServ.modifyAttachment(handle, attach);
					} else {
						charServ.addAttachment(handle, Type.CHARACTER, Format.IMAGE, null, model.getImage());
					}
				} else if (handle!=null) {
					Attachment attach = handle.getFirstAttachment(Type.CHARACTER, Format.IMAGE);
					if (attach!=null) {
						logger.info("Delete old character image");
						charServ.removeAttachment(handle, attach);
					}
				}
			} catch (IOException e) {
				logger.error("Failed modifying portrait attachment",e);
			}
		} finally {
			logger.debug("STOP : saveCharacter");
		}
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.ManagedScreen#backSelected()
	 */
	@Override
	public CloseType backSelected() {
		logger.debug("backSelected()");

		logger.debug("View mode is "+mode);
		if (mode==ViewMode.GENERATION) {
			SpliMoCharacterGenerator generator = (SpliMoCharacterGenerator)control;
			if (!generator.hasEnoughData()) {
				logger.warn("Generator has not enough data");
				CloseType dialog = manager.showAlertAndCall(AlertType.ERROR,
						uiResources.getString("error.chargen.missingdata.title"),
						uiResources.getString("error.chargen.missingdata.text"));
				return null;
			}
			logger.debug("call generate()");
			if ( ((SpliMoCharacterGenerator)control).generate() == null ) {
				// Failed to write character
				logger.fatal("Failed writing character - don't close CharacterViewScreen");
				return null;
			}
			logger.debug("generate() finished");
		} else {
			/*
			 * Write all made modifications to character
			 */
			logger.debug("Add modifications to character log");
			((CharacterLeveller)control).updateHistory();

		}

		return CloseType.APPLY;
//		CloseType choice = manager.showAlertAndCall(AlertType.CONFIRMATION,
//						uiResources.getString("check.chargen.editing.abort.title"),
//						uiResources.getString("check.chargen.editing.abort.text")
//				);
//		logger.debug("Choice was "+choice);
//		if (choice==CloseType.YES) {
//			logger.info("User confirmed changes to character");
//			saveCharacter();
//			return CloseType.APPLY;
//		} else if (choice==CloseType.NO) {
//			logger.info("User rejected changes to character");
//			return CloseType.CANCEL;
//		}
//
//		logger.error("Unexpected type of response: "+choice);
//		return null;
	}

	//-------------------------------------------------------------------
	public void childClosed(ManagedScreen child, CloseType type) {
		logger.debug("************childClosed("+child+", "+type+") not overwritten***************");
		child.setCloseType(type);
	}

	//-------------------------------------------------------------------
	/**
	 * @see org.prelle.javafx.fluent.CloseableContent#close()
	 */
	@Override
	public void close() {
		logger.debug("closing");
		GenerationEventDispatcher.clear();

		/*
		 * If the character has been saved before, ask if changes should be saved again
		 * when screen is closed
		 */
		if (handle!=null) {
			CloseType save = manager.showAlertAndCall(AlertType.CONFIRMATION, uiResources.getString("check.chargen.editing.abort.title"), uiResources.getString("check.chargen.editing.abort.text"));
			logger.debug("ask player for saving character returns "+save);

			if (save==CloseType.YES) {
				/*
				 * 1. Call plugin to encode character
				 * 2. Use character service to save character
				 */
				logger.debug("encode character "+model.getName()+" in handle "+handle);
				CommandResult result = CommandBus.fireCommand(this, CommandType.ENCODE,
						handle.getRuleIdentifier(),
						model
						);
				if (!result.wasProcessed()) {
					logger.error("Cannot save character, since encoding failed");
					manager.showAlertAndCall(
							AlertType.ERROR,
							"Das hätte nicht passieren dürfen",
							"Es hat sich kein Plugin gefunden, welches das Kodieren von Charakteren dieses Systems erlaubt."
					);
				} else {
					byte[] encoded = (byte[]) result.getReturnValue();
					try {
						logger.info("Save character "+model.getName());
						RPGFrameworkLoader.getInstance().getCharacterAndRules().getCharacterService().addAttachment(handle, Type.CHARACTER, Format.RULESPECIFIC, null, encoded);
						logger.info("Saved character "+model.getName()+" successfully");
						// Check for renaming
						if (!model.getName().equals(handle.getName())) {
							logger.warn("TODO: character has been renamed from "+handle.getName()+" to "+model.getName());
						}
					} catch (IOException e) {
						logger.error("Failed saving character",e);
						BabylonEventBus.fireEvent(BabylonEventType.UI_MESSAGE, 2, "Failed saving character.\n"+e);
					}
				}
				close(CloseType.APPLY);
			} else
				close(CloseType.CANCEL);
		} else {
			logger.info("Currently no safety question here");
//			CloseType save = manager.showAlertAndCall(AlertType.CONFIRMATION,
//					uiResources.getString("check.chargen.creation.abort.title"),
//					uiResources.getString("check.chargen.creation.abort.text")
//			);
//			logger.debug("ask player for saving character returns "+save);

		}
	}

	//-------------------------------------------------------------------
	@Override
	public boolean close(CloseType type) {
		logger.info("close("+type+")");
		if (type==CloseType.APPLY) {
			logger.warn("APPLY");
		}else {
			logger.info("remove parsed character and force reloading");
			try {
				handle.setCharacter(null);
			} catch (IOException e) {
			}
		}
		return true;
	}
}
