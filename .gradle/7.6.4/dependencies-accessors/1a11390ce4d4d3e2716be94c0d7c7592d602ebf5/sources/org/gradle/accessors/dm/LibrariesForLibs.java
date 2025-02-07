package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
*/
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final AmazeLibraryAccessors laccForAmazeLibraryAccessors = new AmazeLibraryAccessors(owner);
    private final AndroidXLibraryAccessors laccForAndroidXLibraryAccessors = new AndroidXLibraryAccessors(owner);
    private final ApacheLibraryAccessors laccForApacheLibraryAccessors = new ApacheLibraryAccessors(owner);
    private final AutoLibraryAccessors laccForAutoLibraryAccessors = new AutoLibraryAccessors(owner);
    private final BcpkixLibraryAccessors laccForBcpkixLibraryAccessors = new BcpkixLibraryAccessors(owner);
    private final BcprovLibraryAccessors laccForBcprovLibraryAccessors = new BcprovLibraryAccessors(owner);
    private final CloudrailLibraryAccessors laccForCloudrailLibraryAccessors = new CloudrailLibraryAccessors(owner);
    private final CommonsLibraryAccessors laccForCommonsLibraryAccessors = new CommonsLibraryAccessors(owner);
    private final ConcurrentLibraryAccessors laccForConcurrentLibraryAccessors = new ConcurrentLibraryAccessors(owner);
    private final GlideLibraryAccessors laccForGlideLibraryAccessors = new GlideLibraryAccessors(owner);
    private final GoogleLibraryAccessors laccForGoogleLibraryAccessors = new GoogleLibraryAccessors(owner);
    private final JcifsLibraryAccessors laccForJcifsLibraryAccessors = new JcifsLibraryAccessors(owner);
    private final KotlinLibraryAccessors laccForKotlinLibraryAccessors = new KotlinLibraryAccessors(owner);
    private final LeakcanaryLibraryAccessors laccForLeakcanaryLibraryAccessors = new LeakcanaryLibraryAccessors(owner);
    private final LibsuLibraryAccessors laccForLibsuLibraryAccessors = new LibsuLibraryAccessors(owner);
    private final LogbackLibraryAccessors laccForLogbackLibraryAccessors = new LogbackLibraryAccessors(owner);
    private final MaterialdialogsLibraryAccessors laccForMaterialdialogsLibraryAccessors = new MaterialdialogsLibraryAccessors(owner);
    private final MockitoLibraryAccessors laccForMockitoLibraryAccessors = new MockitoLibraryAccessors(owner);
    private final RobolectricLibraryAccessors laccForRobolectricLibraryAccessors = new RobolectricLibraryAccessors(owner);
    private final RoomLibraryAccessors laccForRoomLibraryAccessors = new RoomLibraryAccessors(owner);
    private final Slf4jLibraryAccessors laccForSlf4jLibraryAccessors = new Slf4jLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

        /**
         * Creates a dependency provider for aboutLibraries (com.mikepenz:aboutlibraries)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAboutLibraries() { return create("aboutLibraries"); }

        /**
         * Creates a dependency provider for awaitility (org.awaitility:awaitility)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAwaitility() { return create("awaitility"); }

        /**
         * Creates a dependency provider for eventbus (org.greenrobot:eventbus)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getEventbus() { return create("eventbus"); }

        /**
         * Creates a dependency provider for gson (com.google.code.gson:gson)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() { return create("gson"); }

        /**
         * Creates a dependency provider for jsoup (org.jsoup:jsoup)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJsoup() { return create("jsoup"); }

        /**
         * Creates a dependency provider for junit (junit:junit)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunit() { return create("junit"); }

        /**
         * Creates a dependency provider for junrar (com.github.junrar:junrar)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunrar() { return create("junrar"); }

        /**
         * Creates a dependency provider for mockk (io.mockk:mockk)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMockk() { return create("mockk"); }

        /**
         * Creates a dependency provider for mpAndroidChart (com.github.PhilJay:MPAndroidChart)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMpAndroidChart() { return create("mpAndroidChart"); }

        /**
         * Creates a dependency provider for okhttp (com.squareup.okhttp3:okhttp)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getOkhttp() { return create("okhttp"); }

        /**
         * Creates a dependency provider for rxandroid (io.reactivex.rxjava2:rxandroid)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRxandroid() { return create("rxandroid"); }

        /**
         * Creates a dependency provider for rxjava (io.reactivex.rxjava2:rxjava)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRxjava() { return create("rxjava"); }

        /**
         * Creates a dependency provider for speedDial (com.leinardi.android:speed-dial)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSpeedDial() { return create("speedDial"); }

        /**
         * Creates a dependency provider for sshj (com.hierynomus:sshj)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSshj() { return create("sshj"); }

        /**
         * Creates a dependency provider for systembarTint (com.readystatesoftware.systembartint:systembartint)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSystembarTint() { return create("systembarTint"); }

        /**
         * Creates a dependency provider for xz (org.tukaani:xz)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getXz() { return create("xz"); }

        /**
         * Creates a dependency provider for zip4j (net.lingala.zip4j:zip4j)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getZip4j() { return create("zip4j"); }

    /**
     * Returns the group of libraries at amaze
     */
    public AmazeLibraryAccessors getAmaze() { return laccForAmazeLibraryAccessors; }

    /**
     * Returns the group of libraries at androidX
     */
    public AndroidXLibraryAccessors getAndroidX() { return laccForAndroidXLibraryAccessors; }

    /**
     * Returns the group of libraries at apache
     */
    public ApacheLibraryAccessors getApache() { return laccForApacheLibraryAccessors; }

    /**
     * Returns the group of libraries at auto
     */
    public AutoLibraryAccessors getAuto() { return laccForAutoLibraryAccessors; }

    /**
     * Returns the group of libraries at bcpkix
     */
    public BcpkixLibraryAccessors getBcpkix() { return laccForBcpkixLibraryAccessors; }

    /**
     * Returns the group of libraries at bcprov
     */
    public BcprovLibraryAccessors getBcprov() { return laccForBcprovLibraryAccessors; }

    /**
     * Returns the group of libraries at cloudrail
     */
    public CloudrailLibraryAccessors getCloudrail() { return laccForCloudrailLibraryAccessors; }

    /**
     * Returns the group of libraries at commons
     */
    public CommonsLibraryAccessors getCommons() { return laccForCommonsLibraryAccessors; }

    /**
     * Returns the group of libraries at concurrent
     */
    public ConcurrentLibraryAccessors getConcurrent() { return laccForConcurrentLibraryAccessors; }

    /**
     * Returns the group of libraries at glide
     */
    public GlideLibraryAccessors getGlide() { return laccForGlideLibraryAccessors; }

    /**
     * Returns the group of libraries at google
     */
    public GoogleLibraryAccessors getGoogle() { return laccForGoogleLibraryAccessors; }

    /**
     * Returns the group of libraries at jcifs
     */
    public JcifsLibraryAccessors getJcifs() { return laccForJcifsLibraryAccessors; }

    /**
     * Returns the group of libraries at kotlin
     */
    public KotlinLibraryAccessors getKotlin() { return laccForKotlinLibraryAccessors; }

    /**
     * Returns the group of libraries at leakcanary
     */
    public LeakcanaryLibraryAccessors getLeakcanary() { return laccForLeakcanaryLibraryAccessors; }

    /**
     * Returns the group of libraries at libsu
     */
    public LibsuLibraryAccessors getLibsu() { return laccForLibsuLibraryAccessors; }

    /**
     * Returns the group of libraries at logback
     */
    public LogbackLibraryAccessors getLogback() { return laccForLogbackLibraryAccessors; }

    /**
     * Returns the group of libraries at materialdialogs
     */
    public MaterialdialogsLibraryAccessors getMaterialdialogs() { return laccForMaterialdialogsLibraryAccessors; }

    /**
     * Returns the group of libraries at mockito
     */
    public MockitoLibraryAccessors getMockito() { return laccForMockitoLibraryAccessors; }

    /**
     * Returns the group of libraries at robolectric
     */
    public RobolectricLibraryAccessors getRobolectric() { return laccForRobolectricLibraryAccessors; }

    /**
     * Returns the group of libraries at room
     */
    public RoomLibraryAccessors getRoom() { return laccForRoomLibraryAccessors; }

    /**
     * Returns the group of libraries at slf4j
     */
    public Slf4jLibraryAccessors getSlf4j() { return laccForSlf4jLibraryAccessors; }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() { return vaccForVersionAccessors; }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() { return baccForBundleAccessors; }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() { return paccForPluginAccessors; }

    public static class AmazeLibraryAccessors extends SubDependencyFactory {

        public AmazeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for trashBin (com.github.TeamAmaze:AmazeTrashBin)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTrashBin() { return create("amaze.trashBin"); }

    }

    public static class AndroidXLibraryAccessors extends SubDependencyFactory {
        private final AndroidXCoreLibraryAccessors laccForAndroidXCoreLibraryAccessors = new AndroidXCoreLibraryAccessors(owner);
        private final AndroidXFragmentLibraryAccessors laccForAndroidXFragmentLibraryAccessors = new AndroidXFragmentLibraryAccessors(owner);
        private final AndroidXTestLibraryAccessors laccForAndroidXTestLibraryAccessors = new AndroidXTestLibraryAccessors(owner);
        private final AndroidXVectordrawableLibraryAccessors laccForAndroidXVectordrawableLibraryAccessors = new AndroidXVectordrawableLibraryAccessors(owner);

        public AndroidXLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for annotation (androidx.annotation:annotation)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAnnotation() { return create("androidX.annotation"); }

            /**
             * Creates a dependency provider for appcompat (androidx.appcompat:appcompat)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAppcompat() { return create("androidX.appcompat"); }

            /**
             * Creates a dependency provider for biometric (androidx.biometric:biometric)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getBiometric() { return create("androidX.biometric"); }

            /**
             * Creates a dependency provider for cardview (androidx.cardview:cardview)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCardview() { return create("androidX.cardview"); }

            /**
             * Creates a dependency provider for constraintLayout (androidx.constraintlayout:constraintlayout)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getConstraintLayout() { return create("androidX.constraintLayout"); }

            /**
             * Creates a dependency provider for legacySupportV13 (androidx.legacy:legacy-support-v13)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getLegacySupportV13() { return create("androidX.legacySupportV13"); }

            /**
             * Creates a dependency provider for material (com.google.android.material:material)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getMaterial() { return create("androidX.material"); }

            /**
             * Creates a dependency provider for multidex (androidx.multidex:multidex)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getMultidex() { return create("androidX.multidex"); }

            /**
             * Creates a dependency provider for palette (androidx.palette:palette-ktx)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getPalette() { return create("androidX.palette"); }

            /**
             * Creates a dependency provider for preference (androidx.preference:preference-ktx)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getPreference() { return create("androidX.preference"); }

        /**
         * Returns the group of libraries at androidX.core
         */
        public AndroidXCoreLibraryAccessors getCore() { return laccForAndroidXCoreLibraryAccessors; }

        /**
         * Returns the group of libraries at androidX.fragment
         */
        public AndroidXFragmentLibraryAccessors getFragment() { return laccForAndroidXFragmentLibraryAccessors; }

        /**
         * Returns the group of libraries at androidX.test
         */
        public AndroidXTestLibraryAccessors getTest() { return laccForAndroidXTestLibraryAccessors; }

        /**
         * Returns the group of libraries at androidX.vectordrawable
         */
        public AndroidXVectordrawableLibraryAccessors getVectordrawable() { return laccForAndroidXVectordrawableLibraryAccessors; }

    }

    public static class AndroidXCoreLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public AndroidXCoreLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (androidx.core:core-ktx)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("androidX.core"); }

            /**
             * Creates a dependency provider for testing (androidx.arch.core:core-testing)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTesting() { return create("androidX.core.testing"); }

    }

    public static class AndroidXFragmentLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public AndroidXFragmentLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for fragment (androidx.fragment:fragment-ktx)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("androidX.fragment"); }

            /**
             * Creates a dependency provider for testing (androidx.fragment:fragment-testing)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTesting() { return create("androidX.fragment.testing"); }

    }

    public static class AndroidXTestLibraryAccessors extends SubDependencyFactory {
        private final AndroidXTestExtLibraryAccessors laccForAndroidXTestExtLibraryAccessors = new AndroidXTestExtLibraryAccessors(owner);

        public AndroidXTestLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (androidx.test:core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("androidX.test.core"); }

            /**
             * Creates a dependency provider for expresso (androidx.test.espresso:espresso-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getExpresso() { return create("androidX.test.expresso"); }

            /**
             * Creates a dependency provider for rules (androidx.test:rules)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRules() { return create("androidX.test.rules"); }

            /**
             * Creates a dependency provider for runner (androidx.test:runner)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRunner() { return create("androidX.test.runner"); }

            /**
             * Creates a dependency provider for uiautomator (androidx.test.uiautomator:uiautomator)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getUiautomator() { return create("androidX.test.uiautomator"); }

        /**
         * Returns the group of libraries at androidX.test.ext
         */
        public AndroidXTestExtLibraryAccessors getExt() { return laccForAndroidXTestExtLibraryAccessors; }

    }

    public static class AndroidXTestExtLibraryAccessors extends SubDependencyFactory {

        public AndroidXTestExtLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for junit (androidx.test.ext:junit)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJunit() { return create("androidX.test.ext.junit"); }

    }

    public static class AndroidXVectordrawableLibraryAccessors extends SubDependencyFactory {

        public AndroidXVectordrawableLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for animated (androidx.vectordrawable:vectordrawable-animated)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAnimated() { return create("androidX.vectordrawable.animated"); }

    }

    public static class ApacheLibraryAccessors extends SubDependencyFactory {
        private final ApacheFtpserverLibraryAccessors laccForApacheFtpserverLibraryAccessors = new ApacheFtpserverLibraryAccessors(owner);
        private final ApacheMinaLibraryAccessors laccForApacheMinaLibraryAccessors = new ApacheMinaLibraryAccessors(owner);

        public ApacheLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for sshd (org.apache.sshd:sshd-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getSshd() { return create("apache.sshd"); }

        /**
         * Returns the group of libraries at apache.ftpserver
         */
        public ApacheFtpserverLibraryAccessors getFtpserver() { return laccForApacheFtpserverLibraryAccessors; }

        /**
         * Returns the group of libraries at apache.mina
         */
        public ApacheMinaLibraryAccessors getMina() { return laccForApacheMinaLibraryAccessors; }

    }

    public static class ApacheFtpserverLibraryAccessors extends SubDependencyFactory {
        private final ApacheFtpserverFtpletLibraryAccessors laccForApacheFtpserverFtpletLibraryAccessors = new ApacheFtpserverFtpletLibraryAccessors(owner);

        public ApacheFtpserverLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.apache.ftpserver:ftpserver-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("apache.ftpserver.core"); }

        /**
         * Returns the group of libraries at apache.ftpserver.ftplet
         */
        public ApacheFtpserverFtpletLibraryAccessors getFtplet() { return laccForApacheFtpserverFtpletLibraryAccessors; }

    }

    public static class ApacheFtpserverFtpletLibraryAccessors extends SubDependencyFactory {

        public ApacheFtpserverFtpletLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for api (org.apache.ftpserver:ftplet-api)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getApi() { return create("apache.ftpserver.ftplet.api"); }

    }

    public static class ApacheMinaLibraryAccessors extends SubDependencyFactory {

        public ApacheMinaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.apache.mina:mina-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("apache.mina.core"); }

    }

    public static class AutoLibraryAccessors extends SubDependencyFactory {

        public AutoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for service (com.google.auto.service:auto-service)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getService() { return create("auto.service"); }

    }

    public static class BcpkixLibraryAccessors extends SubDependencyFactory {

        public BcpkixLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jdk18on (org.bouncycastle:bcpkix-jdk18on)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJdk18on() { return create("bcpkix.jdk18on"); }

    }

    public static class BcprovLibraryAccessors extends SubDependencyFactory {

        public BcprovLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jdk18on (org.bouncycastle:bcprov-jdk18on)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJdk18on() { return create("bcprov.jdk18on"); }

    }

    public static class CloudrailLibraryAccessors extends SubDependencyFactory {
        private final CloudrailSiLibraryAccessors laccForCloudrailSiLibraryAccessors = new CloudrailSiLibraryAccessors(owner);

        public CloudrailLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at cloudrail.si
         */
        public CloudrailSiLibraryAccessors getSi() { return laccForCloudrailSiLibraryAccessors; }

    }

    public static class CloudrailSiLibraryAccessors extends SubDependencyFactory {

        public CloudrailSiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for android (com.cloudrail:cloudrail-si-android)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAndroid() { return create("cloudrail.si.android"); }

    }

    public static class CommonsLibraryAccessors extends SubDependencyFactory {

        public CommonsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for compress (org.apache.commons:commons-compress)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompress() { return create("commons.compress"); }

            /**
             * Creates a dependency provider for net (commons-net:commons-net)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getNet() { return create("commons.net"); }

    }

    public static class ConcurrentLibraryAccessors extends SubDependencyFactory {

        public ConcurrentLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for trees (com.github.npgall:concurrent-trees)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTrees() { return create("concurrent.trees"); }

    }

    public static class GlideLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public GlideLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for glide (com.github.bumptech.glide:glide)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("glide"); }

            /**
             * Creates a dependency provider for ksp (com.github.bumptech.glide:ksp)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getKsp() { return create("glide.ksp"); }

            /**
             * Creates a dependency provider for recyclerView (com.github.bumptech.glide:recyclerview-integration)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRecyclerView() { return create("glide.recyclerView"); }

    }

    public static class GoogleLibraryAccessors extends SubDependencyFactory {
        private final GooglePlayLibraryAccessors laccForGooglePlayLibraryAccessors = new GooglePlayLibraryAccessors(owner);

        public GoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at google.play
         */
        public GooglePlayLibraryAccessors getPlay() { return laccForGooglePlayLibraryAccessors; }

    }

    public static class GooglePlayLibraryAccessors extends SubDependencyFactory {

        public GooglePlayLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for billing (com.android.billingclient:billing)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getBilling() { return create("google.play.billing"); }

    }

    public static class JcifsLibraryAccessors extends SubDependencyFactory {

        public JcifsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for ng (eu.agno3.jcifs:jcifs-ng)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getNg() { return create("jcifs.ng"); }

    }

    public static class KotlinLibraryAccessors extends SubDependencyFactory {
        private final KotlinCoroutineLibraryAccessors laccForKotlinCoroutineLibraryAccessors = new KotlinCoroutineLibraryAccessors(owner);
        private final KotlinStdlibLibraryAccessors laccForKotlinStdlibLibraryAccessors = new KotlinStdlibLibraryAccessors(owner);

        public KotlinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Returns the group of libraries at kotlin.coroutine
         */
        public KotlinCoroutineLibraryAccessors getCoroutine() { return laccForKotlinCoroutineLibraryAccessors; }

        /**
         * Returns the group of libraries at kotlin.stdlib
         */
        public KotlinStdlibLibraryAccessors getStdlib() { return laccForKotlinStdlibLibraryAccessors; }

    }

    public static class KotlinCoroutineLibraryAccessors extends SubDependencyFactory {

        public KotlinCoroutineLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for test (org.jetbrains.kotlinx:kotlinx-coroutines-test)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTest() { return create("kotlin.coroutine.test"); }

    }

    public static class KotlinStdlibLibraryAccessors extends SubDependencyFactory {

        public KotlinStdlibLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jdk8 (org.jetbrains.kotlin:kotlin-stdlib-jdk8)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJdk8() { return create("kotlin.stdlib.jdk8"); }

    }

    public static class LeakcanaryLibraryAccessors extends SubDependencyFactory {

        public LeakcanaryLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for android (com.squareup.leakcanary:leakcanary-android)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAndroid() { return create("leakcanary.android"); }

    }

    public static class LibsuLibraryAccessors extends SubDependencyFactory {

        public LibsuLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (com.github.topjohnwu.libsu:core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("libsu.core"); }

            /**
             * Creates a dependency provider for io (com.github.topjohnwu.libsu:io)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getIo() { return create("libsu.io"); }

    }

    public static class LogbackLibraryAccessors extends SubDependencyFactory {

        public LogbackLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for android (com.github.tony19:logback-android)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAndroid() { return create("logback.android"); }

            /**
             * Creates a dependency provider for classic (ch.qos.logback:logback-classic)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getClassic() { return create("logback.classic"); }

    }

    public static class MaterialdialogsLibraryAccessors extends SubDependencyFactory {

        public MaterialdialogsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for commons (com.afollestad.material-dialogs:commons)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCommons() { return create("materialdialogs.commons"); }

            /**
             * Creates a dependency provider for core (com.afollestad.material-dialogs:core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("materialdialogs.core"); }

    }

    public static class MockitoLibraryAccessors extends SubDependencyFactory {

        public MockitoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.mockito:mockito-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("mockito.core"); }

            /**
             * Creates a dependency provider for inline (org.mockito:mockito-inline)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getInline() { return create("mockito.inline"); }

            /**
             * Creates a dependency provider for kotlin (org.mockito.kotlin:mockito-kotlin)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getKotlin() { return create("mockito.kotlin"); }

    }

    public static class RobolectricLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final RobolectricShadowsLibraryAccessors laccForRobolectricShadowsLibraryAccessors = new RobolectricShadowsLibraryAccessors(owner);

        public RobolectricLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for robolectric (org.robolectric:robolectric)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("robolectric"); }

        /**
         * Returns the group of libraries at robolectric.shadows
         */
        public RobolectricShadowsLibraryAccessors getShadows() { return laccForRobolectricShadowsLibraryAccessors; }

    }

    public static class RobolectricShadowsLibraryAccessors extends SubDependencyFactory {

        public RobolectricShadowsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for httpclient (org.robolectric:shadows-httpclient)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getHttpclient() { return create("robolectric.shadows.httpclient"); }

    }

    public static class RoomLibraryAccessors extends SubDependencyFactory {

        public RoomLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for compiler (androidx.room:room-compiler)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompiler() { return create("room.compiler"); }

            /**
             * Creates a dependency provider for migration (androidx.room:room-migration)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getMigration() { return create("room.migration"); }

            /**
             * Creates a dependency provider for runtime (androidx.room:room-runtime)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRuntime() { return create("room.runtime"); }

            /**
             * Creates a dependency provider for rxjava2 (androidx.room:room-rxjava2)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRxjava2() { return create("room.rxjava2"); }

    }

    public static class Slf4jLibraryAccessors extends SubDependencyFactory {

        public Slf4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for api (org.slf4j:slf4j-api)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getApi() { return create("slf4j.api"); }

    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: aboutLibraries (6.1.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAboutLibraries() { return getVersion("aboutLibraries"); }

            /**
             * Returns the version associated to this alias: amazeTrashBin (1.0.10)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAmazeTrashBin() { return getVersion("amazeTrashBin"); }

            /**
             * Returns the version associated to this alias: androidBilling (5.0.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidBilling() { return getVersion("androidBilling"); }

            /**
             * Returns the version associated to this alias: androidMaterial (1.5.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidMaterial() { return getVersion("androidMaterial"); }

            /**
             * Returns the version associated to this alias: androidXAnnotation (1.7.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXAnnotation() { return getVersion("androidXAnnotation"); }

            /**
             * Returns the version associated to this alias: androidXAppCompat (1.6.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXAppCompat() { return getVersion("androidXAppCompat"); }

            /**
             * Returns the version associated to this alias: androidXArchCoreTest (2.2.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXArchCoreTest() { return getVersion("androidXArchCoreTest"); }

            /**
             * Returns the version associated to this alias: androidXBiometric (1.1.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXBiometric() { return getVersion("androidXBiometric"); }

            /**
             * Returns the version associated to this alias: androidXCardView (1.0.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXCardView() { return getVersion("androidXCardView"); }

            /**
             * Returns the version associated to this alias: androidXConstraintLayout (1.1.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXConstraintLayout() { return getVersion("androidXConstraintLayout"); }

            /**
             * Returns the version associated to this alias: androidXCore (1.7.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXCore() { return getVersion("androidXCore"); }

            /**
             * Returns the version associated to this alias: androidXFragment (1.5.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXFragment() { return getVersion("androidXFragment"); }

            /**
             * Returns the version associated to this alias: androidXMultidex (2.0.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXMultidex() { return getVersion("androidXMultidex"); }

            /**
             * Returns the version associated to this alias: androidXPalette (1.0.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXPalette() { return getVersion("androidXPalette"); }

            /**
             * Returns the version associated to this alias: androidXPref (1.2.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXPref() { return getVersion("androidXPref"); }

            /**
             * Returns the version associated to this alias: androidXTest (1.5.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXTest() { return getVersion("androidXTest"); }

            /**
             * Returns the version associated to this alias: androidXTestExt (1.1.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXTestExt() { return getVersion("androidXTestExt"); }

            /**
             * Returns the version associated to this alias: androidXTestRunner (1.5.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAndroidXTestRunner() { return getVersion("androidXTestRunner"); }

            /**
             * Returns the version associated to this alias: apacheMina (2.0.16)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getApacheMina() { return getVersion("apacheMina"); }

            /**
             * Returns the version associated to this alias: apacheSshd (1.7.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getApacheSshd() { return getVersion("apacheSshd"); }

            /**
             * Returns the version associated to this alias: autoService (1.0-rc4)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAutoService() { return getVersion("autoService"); }

            /**
             * Returns the version associated to this alias: awaitility (3.1.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAwaitility() { return getVersion("awaitility"); }

            /**
             * Returns the version associated to this alias: bouncyCastle (1.76)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getBouncyCastle() { return getVersion("bouncyCastle"); }

            /**
             * Returns the version associated to this alias: cloudrailSiAndroid (2.22.4)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCloudrailSiAndroid() { return getVersion("cloudrailSiAndroid"); }

            /**
             * Returns the version associated to this alias: commonsCompress (1.22)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCommonsCompress() { return getVersion("commonsCompress"); }

            /**
             * Returns the version associated to this alias: commonsNet (3.8.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCommonsNet() { return getVersion("commonsNet"); }

            /**
             * Returns the version associated to this alias: compileSdk (33)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCompileSdk() { return getVersion("compileSdk"); }

            /**
             * Returns the version associated to this alias: concurrentTrees (2.6.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getConcurrentTrees() { return getVersion("concurrentTrees"); }

            /**
             * Returns the version associated to this alias: espresso (3.5.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getEspresso() { return getVersion("espresso"); }

            /**
             * Returns the version associated to this alias: eventbus (3.3.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getEventbus() { return getVersion("eventbus"); }

            /**
             * Returns the version associated to this alias: fabSpeedDial (3.2.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getFabSpeedDial() { return getVersion("fabSpeedDial"); }

            /**
             * Returns the version associated to this alias: ftpserver (1.1.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getFtpserver() { return getVersion("ftpserver"); }

            /**
             * Returns the version associated to this alias: glide (4.14.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGlide() { return getVersion("glide"); }

            /**
             * Returns the version associated to this alias: gson (2.9.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGson() { return getVersion("gson"); }

            /**
             * Returns the version associated to this alias: jcifs (2.1.9)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJcifs() { return getVersion("jcifs"); }

            /**
             * Returns the version associated to this alias: jsoup (1.13.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJsoup() { return getVersion("jsoup"); }

            /**
             * Returns the version associated to this alias: junit (4.13.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJunit() { return getVersion("junit"); }

            /**
             * Returns the version associated to this alias: junrar (7.4.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJunrar() { return getVersion("junrar"); }

            /**
             * Returns the version associated to this alias: kotlin (1.9.10)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlin() { return getVersion("kotlin"); }

            /**
             * Returns the version associated to this alias: kotlinStdlibJdk8 (1.9.20)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlinStdlibJdk8() { return getVersion("kotlinStdlibJdk8"); }

            /**
             * Returns the version associated to this alias: kotlinxCoroutineTest (1.7.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlinxCoroutineTest() { return getVersion("kotlinxCoroutineTest"); }

            /**
             * Returns the version associated to this alias: leakcanaryAndroid (2.7)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLeakcanaryAndroid() { return getVersion("leakcanaryAndroid"); }

            /**
             * Returns the version associated to this alias: legacySupportV13 (1.0.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLegacySupportV13() { return getVersion("legacySupportV13"); }

            /**
             * Returns the version associated to this alias: libsu (3.2.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLibsu() { return getVersion("libsu"); }

            /**
             * Returns the version associated to this alias: logbackAndroid (3.0.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLogbackAndroid() { return getVersion("logbackAndroid"); }

            /**
             * Returns the version associated to this alias: logbackClassic (1.2.11)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLogbackClassic() { return getVersion("logbackClassic"); }

            /**
             * Returns the version associated to this alias: materialDialogs (0.9.6.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMaterialDialogs() { return getVersion("materialDialogs"); }

            /**
             * Returns the version associated to this alias: minSdk (33)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMinSdk() { return getVersion("minSdk"); }

            /**
             * Returns the version associated to this alias: mockito (4.11.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMockito() { return getVersion("mockito"); }

            /**
             * Returns the version associated to this alias: mockitoInline (4.11.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMockitoInline() { return getVersion("mockitoInline"); }

            /**
             * Returns the version associated to this alias: mockitoKotlin (4.1.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMockitoKotlin() { return getVersion("mockitoKotlin"); }

            /**
             * Returns the version associated to this alias: mockk (1.12.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMockk() { return getVersion("mockk"); }

            /**
             * Returns the version associated to this alias: mpAndroidChart (v3.0.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMpAndroidChart() { return getVersion("mpAndroidChart"); }

            /**
             * Returns the version associated to this alias: okHttp (4.9.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getOkHttp() { return getVersion("okHttp"); }

            /**
             * Returns the version associated to this alias: robolectric (4.9)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRobolectric() { return getVersion("robolectric"); }

            /**
             * Returns the version associated to this alias: room (2.5.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRoom() { return getVersion("room"); }

            /**
             * Returns the version associated to this alias: rxAndroid (2.1.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRxAndroid() { return getVersion("rxAndroid"); }

            /**
             * Returns the version associated to this alias: rxJava (2.2.9)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRxJava() { return getVersion("rxJava"); }

            /**
             * Returns the version associated to this alias: slf4j (2.0.7)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSlf4j() { return getVersion("slf4j"); }

            /**
             * Returns the version associated to this alias: sshj (0.35.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSshj() { return getVersion("sshj"); }

            /**
             * Returns the version associated to this alias: systembartint (1.0.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSystembartint() { return getVersion("systembartint"); }

            /**
             * Returns the version associated to this alias: targetSdk (33)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getTargetSdk() { return getVersion("targetSdk"); }

            /**
             * Returns the version associated to this alias: uiAutomator (2.2.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getUiAutomator() { return getVersion("uiAutomator"); }

            /**
             * Returns the version associated to this alias: vectordrawableAnimated (1.1.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getVectordrawableAnimated() { return getVersion("vectordrawableAnimated"); }

            /**
             * Returns the version associated to this alias: xz (1.9)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getXz() { return getVersion("xz"); }

            /**
             * Returns the version associated to this alias: zip4j (2.6.4)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getZip4j() { return getVersion("zip4j"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
