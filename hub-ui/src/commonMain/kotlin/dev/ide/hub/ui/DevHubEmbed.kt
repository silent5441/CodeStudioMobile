package dev.ide.hub.ui

/**
 * The bundled DevHub seed catalog, guaranteed-available copy. The primary store path reads it as a
 * classpath resource (packaged with the app); some packaging situations can drop that asset, so this
 * plain-text copy is the unconditional fallback — it is never "missing" because it is part of the
 * compiled code itself.
 * (Escape note: `$` in the JSON is written as `${$}` in the raw string below.)
 */
const val EMBEDDED_CATALOG_JSON: String = """
{
  "schema": 1,
  "meta": {
    "title": "Code Studio DevHub",
    "source": "bundled-seed"
  },
  "snippets": [
    {
      "id": "ui-button-compose",
      "title": "Button — Compose",
      "description": "Material 3 Button variants: FilledButton, OutlinedButton, TextButton, IconButton, and a custom-shaped button.",
      "category": "UI",
      "tags": [
        "ui",
        "button",
        "m3"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "@Composable\nfun ExampleButton() {\n    FilledButton(onClick = { /* do it */ }) {\n        Text(\"Primary\")\n    }\n    OutlinedButton(onClick = { }) {\n        Text(\"Outlined\")\n    }\n    TextButton(onClick = { }) {\n        Text(\"Text only\")\n    }\n    IconButton(onClick = { }) {\n        Icon(Icons.Default.Star, contentDescription = \"Star\")\n    }\n    // Custom shape + colors\n    Button(\n        onClick = { },\n        shape = RoundedCornerShape(16.dp),\n        colors = ButtonDefaults.buttonColors(\n            containerColor = MaterialTheme.colorScheme.primary,\n            contentColor = MaterialTheme.colorScheme.onPrimary\n        ),\n        modifier = Modifier.fillMaxWidth()\n    ) { Text(\"Full width, rounded\") }\n}"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-button-xml",
      "title": "Button — XML",
      "description": "Android View Button with states (ripple, disabled) from XML.",
      "category": "UI",
      "tags": [
        "ui",
        "button",
        "xml"
      ],
      "implementations": [
        {
          "language": "xml",
          "technology": "Android Views",
          "code": "<Button\n    android:id=\"@+id/btnPrimary\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"wrap_content\"\n    android:text=\"Primary\"\n    android:textAllCaps=\"true\"\n    android:backgroundTint=\"@color/primary\"\n    android:textColor=\"@color/white\"\n    android:padding=\"12dp\" />"
        }
      ],
      "dependencies": [],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-button-kotlin",
      "title": "Button — Kotlin (Views)",
      "description": "Programmatic MaterialButton in Kotlin with click listener and styling.",
      "category": "UI",
      "tags": [
        "ui",
        "button",
        "views"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Android Views",
          "code": "val button = MaterialButton(this).apply {\n    text = \"Press me\"\n    setOnClickListener { toast(\"Clicked\") }\n    backgroundTintList = ColorStateList.valueOf(\n        ContextCompat.getColor(this@MainActivity, R.color.primary)\n    )\n    cornerRadius = 12\n}\nparent.addView(\n    button,\n    FrameLayout.LayoutParams(\n        FrameLayout.LayoutParams.MATCH_PARENT,\n        FrameLayout.LayoutParams.WRAP_CONTENT\n    )\n)"
        }
      ],
      "dependencies": [
        "com.google.android.material:material"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-textfield",
      "title": "TextField & OutlinedTextField",
      "description": "Compose text input with label, keyboard options, and validation helper.",
      "category": "UI",
      "tags": [
        "input",
        "textfield",
        "forms"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "@Composable\nfun EmailField() {\n    var email by remember { mutableStateOf(\"\") }\n    var error by remember { mutableStateOf(false) }\n\n    OutlinedTextField(\n        value = email,\n        onValueChange = {\n            email = it\n            error = !email.isEmail()\n        },\n        label = { Text(\"Email\") },\n        leadingIcon = { Icon(Icons.Default.Email, null) },\n        singleLine = true,\n        isError = error,\n        supportingText = {\n            if (error) Text(\"Enter a valid email\")\n        },\n        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),\n        modifier = Modifier.fillMaxWidth()\n    )\n}\n\nfun String.isEmail() = this.contains(\"@\") && this.contains(\".\")"
        }
      ],
      "dependencies": [],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-card",
      "title": "Card & ElevatedCard",
      "description": "M3 cards: elevation, clicks, leading media and rounded corners.",
      "category": "UI",
      "tags": [
        "card",
        "surface"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "@Composable\nfun MediaCard() {\n    Card(\n        onClick = { }, // clickable card\n        shape = RoundedCornerShape(12.dp),\n        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),\n        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),\n        modifier = Modifier.fillMaxWidth()\n    ) {\n        Column(Modifier.padding(16.dp)) {\n            Text(\"Title\", style = MaterialTheme.typography.titleMedium)\n            Spacer(Modifier.height(4.dp))\n            Text(\"Supporting text goes here...\", style = MaterialTheme.typography.bodyMedium)\n        }\n    }\n\n    ElevatedCard(modifier = Modifier.fillMaxWidth()) {\n        Text(\"Elevated\", modifier = Modifier.padding(12.dp))\n    }\n}"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-card-xml",
      "title": "MaterialCardView — XML",
      "description": "MaterialCardView ... XML me shape, elevation aur stroke ke saath.",
      "category": "UI",
      "tags": [
        "card",
        "views",
        "xml"
      ],
      "implementations": [
        {
          "language": "xml",
          "technology": "Android Views",
          "code": "<com.google.android.material.card.MaterialCardView\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"wrap_content\"\n    app:cardCornerRadius=\"12dp\"\n    app:cardElevation=\"4dp\"\n    app:cardBackgroundColor=\"@color/surfaceVariant\"\n    app:strokeWidth=\"1dp\"\n    app:strokeColor=\"@color/outline\">\n\n    <TextView\n        android:layout_width=\"match_parent\"\n        android:layout_height=\"wrap_content\"\n        android:padding=\"16dp\"\n        android:text=\"Card content\" />\n\n</com.google.android.material.card.MaterialCardView>"
        }
      ],
      "dependencies": [
        "com.google.android.material:material"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-dialog",
      "title": "AlertDialog (M3)",
      "description": "Compose AlertDialog with confirm/dismiss listeners, and the classic Dialog with custom content.",
      "category": "UI",
      "tags": [
        "dialog",
        "alerts"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "@Composable\nfun ConfirmDialog(\n    title: String,\n    message: String,\n    onConfirm: () -> Unit,\n    onDismiss: () -> Unit\n) {\n    AlertDialog(\n        onDismissRequest = onDismiss,\n        title = { Text(title) },\n        text = { Text(message) },\n        confirmButton = {\n            TextButton(onClick = onConfirm) { Text(\"OK\") }\n        },\n        dismissButton = {\n            TextButton(onClick = onDismiss) { Text(\"Cancel\") }\n        },\n        shape = RoundedCornerShape(20.dp)\n    )\n}"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-progress",
      "title": "Progress indicators",
      "description": "LinearProgressIndicator with determinate progress, CircularProgressIndicator, and a full-screen loader.",
      "category": "UI",
      "tags": [
        "progress",
        "loading"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "// Indeterminate bar\nLinearProgressIndicator(\n    modifier = Modifier.fillMaxWidth().padding(16.dp)\n)\n\n// Determinate (percent)\nval progress by remember { mutableFloatStateOf(0.4f) }\nLinearProgressIndicator(\n    progress = { progress },\n    modifier = Modifier.fillMaxWidth().padding(16.dp)\n)\n\n// Circular\nCircularProgressIndicator(modifier = Modifier.size(48.dp))\n\n// Page loading overlay\nBox(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {\n    CircularProgressIndicator(\n        Modifier.align(Alignment.Center),\n        color = MaterialTheme.colorScheme.primary\n    )\n}"
        },
        {
          "language": "xml",
          "technology": "Android Views",
          "code": "<ProgressBar\n    android:id=\"@+id/loader\"\n    android:layout_width=\"wrap_content\"\n    android:layout_height=\"wrap_content\"\n    android:layout_gravity=\"center\" />\n<ProgressBar\n    style=\"?android:attr/progressBarStyleHorizontal\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"wrap_content\"\n    android:max=\"100\"\n    android:progress=\"40\" />"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-animations",
      "title": "Animations — animate*AsState",
      "description": "M3 Compose animates: animateFloatAsState, animateColorAsState, crossfade + EnterTransition/ExitTransition.",
      "category": "Animations",
      "tags": [
        "animation",
        "transition",
        "animate"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "var expanded by remember { mutableStateOf(false) }\n\n// Float animation\nval height by animateFloatAsState(\n    if (expanded) 200f else 80f,\n    animationSpec = tween(300, easing = FastOutSlowInEasing)\n)\n\n// Color animation\nval bg by animateColorAsState(\n    if (expanded) Color(0xFFBBDEFB) else Color.White,\n    tween(400)\n)\n\nBox(\n    Modifier\n        .fillMaxWidth()\n        .height(height.dp)\n        .background(bg, RoundedCornerShape(16.dp))\n        .clickable { expanded = !expanded }\n)\n\n// AnimatedVisibility + slide\nAnimatedVisibility(\n    visible = expanded,\n    enter = slideInVertically { -it } + fadeIn(),\n    exit = slideOutVertically { it } + fadeOut()\n) {\n    Text(\"Revealed content\")\n}"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-anim-view",
      "title": "Animations — Views",
      "description": "ViewPropertyAnimator, ObjectAnimator loop, and crossfade between views.",
      "category": "Animations",
      "tags": [
        "animation",
        "views"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Android Views",
          "code": "// ViewPropertyAnimator — chained, one-liner\nview.animate()\n    .alpha(0.5f)\n    .translationY(-200f)\n    .scaleX(1.2f)\n    .setDuration(300)\n    .withEndAction { view.animate().alpha(1f).start() }\n    .start()\n\n// Crossfade two views\nval content = findViewById<View>(R.id.content)\nval progressBar = findViewById<View>(R.id.loading)\ncontent.visibility = View.VISIBLE\ncontent.animate().alpha(1f).setDuration(200).start()\nprogressBar.animate().alpha(0f).setDuration(200)\n    .withEndAction { progressBar.visibility = View.GONE }\n    .start()\n\n// ObjectAnimator infinite loop\nval rotation = ObjectAnimator.ofFloat(view, \"rotation\", 0f, 360f)\nrotation.duration = 800\nrotation.repeatCount = ObjectAnimator.INFINITE\nrotation.repeatMode = ObjectAnimator.RESTART\nrotation.start()"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-recyclerview",
      "title": "RecyclerView + ListAdapter",
      "description": "ListAdapter with DiffUtil, ViewBinding, and an inline click listener.",
      "category": "UI",
      "tags": [
        "recyclerview",
        "diffutil",
        "list"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Android Views",
          "code": "class ItemAdapter(\n    private val onClick: (Item) -> Unit\n) : ListAdapter<Item, ItemAdapter.Holder>(DIFF) {\n\n    private object DIFF : DiffUtil.ItemCallback<ListItem>() {\n        override fun areItemsTheSame(a: ListItem, b: ListItem) = a.id == b.id\n        override fun areContentsTheSame(a: ListItem, b: ListItem) = a == b\n    }\n\n    class Holder(view: View) : RecyclerView.ViewHolder(view) {\n        val title: TextView = view.findViewById(R.id.title)\n    }\n\n    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =\n        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false))\n\n    override fun onBindViewHolder(h: Holder, position: Int) {\n        val item = getItem(position)\n        h.title.text = item.name\n        h.itemView.setOnClickListener { onClick(item) }\n    }\n\n    @JvmName(\"found\")\n    fun tooltip() = Unit\n}"
        },
        {
          "language": "java",
          "technology": "Android Views",
          "code": "public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Holder> {\n    private final List<Item> items;\n\n    public ItemAdapter(List<Item> items) {\n        this.items = items;\n    }\n\n    @NonNull\n    @Override\n    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {\n        View v = LayoutInflater.from(parent.getContext())\n            .inflate(R.layout.item_row, parent, false);\n        return new Holder(v);\n    }\n\n    @Override\n    public void onBindViewHolder(@NonNull Holder h, int position) {\n        Item item = items.get(position);\n        h.title.setText(item.getName());\n        h.itemView.setOnClickListener(v -> { /* open detail */ });\n    }\n\n    @Override\n    public int getItemCount() { return items.size(); }\n\n    static class Holder extends RecyclerView.ViewHolder {\n        final TextView text;\n        Holder(View v) { super(v); text = v.findViewById(R.id.title); }\n    }\n}"
        }
      ],
      "dependencies": [
        "androidx.recyclerview:recyclerview"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-swiperefresh",
      "title": "SwipeRefresh + LazyColumn",
      "description": "Pull-to-refresh in Compose (material's pull-to-refresh pattern), also the Views version.",
      "category": "UI",
      "tags": [
        "refresh",
        "pull"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "// Material3 pull-to-refresh\nval pullRefreshing by remember { mutableStateOf(false) }\nval refreshState = rememberPullToRefreshState()\n\nBox(Modifier.fillMaxSize().pullToRefresh(\n    isRefreshing = pullRefreshing,\n    state = refreshState,\n    onRefresh = { scope.launch { fetch(); pullRefreshing = false } }\n)) {\n    LazyColumn(Modifier.fillMaxSize()) {\n        items(items) { Row(Modifier.padding(12.dp)) { Text(it.name) } }\n    }\n}"
        },
        {
          "language": "xml",
          "technology": "Android Views",
          "code": "<!-- layout -->\n<androidx.swiperefreshlayout.widget.SwipeRefreshLayout\n    android:id=\"@+id/swipe\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"match_parent\">\n    <androidx.recyclerview.widget.RecyclerView\n        android:id=\"@+id/list\"\n        android:layout_width=\"match_parent\"\n        android:layout_height=\"match_parent\" />\n</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>\n\n<!-- kotlin -->\nswipe.setOnRefreshListener {\n    fetch { swipeRefresh.isRefreshing = false }\n}"
        }
      ],
      "dependencies": [
        "androidx.swiperefreshlayout:swiperefreshlayout"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-lazylist",
      "title": "LazyColumn / LazyRow",
      "description": "Lazy layout with items(), headers, sticky headers and dividers.",
      "category": "UI",
      "tags": [
        "list",
        "lazycolumn"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "LazyColumn(\n    modifier = Modifier.fillMaxSize(),\n    contentPadding = PaddingValues(16.dp),\n    verticalArrangement = Arrangement.spacedBy(8.dp)\n) {\n    item(key = \"header\") {\n        Text(\"Items\", style = MaterialTheme.typography.headlineSmall)\n    }\n    items(items, key = { it.id }) { item ->\n        Text(item.name, modifier = Modifier.fillMaxWidth())\n        HorizontalDivider(thickness = 0.5.dp)\n    }\n    item { TextButton(onClick = { loadMore() }) { Text(\"Load more\") } }\n}\n\nLazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {\n    items(chips) { Label } // horizontal row of chips\n}"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-drawer",
      "title": "NavigationDrawer",
      "description": "M3 modal + permanent drawer scaffold with nav items.",
      "category": "Navigation",
      "tags": [
        "drawer",
        "navigation"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "val drawerState = rememberDrawerState(DrawerValue.Closed)\nval scope = rememberCoroutineScope()\n\nModalNavigationDrawer(\n    drawerState = drawerState,\n    drawerContent = {\n        ModalDrawerSheet {\n            Text(\"Menu\", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)\n            HorizontalDivider()\n            listOf(\"Home\", \"Library\", \"Settings\").forEach { item ->\n                NavigationDrawerItem(\n                    label = { Text(item) },\n                    selected = selected == item,\n                    onClick = {\n                        selected = item\n                        scope.launch { drawerState.close() }\n                    },\n                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)\n                )\n            }\n        }\n    }\n) {\n    // your content\n}"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "nav-compose-navigation",
      "title": "Navigation — Compose",
      "description": "Type-safe navigation: NavHost + routes, back stack, and arguments.",
      "category": "Navigation",
      "tags": [
        "navigation",
        "navhost"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Navigation Compose",
          "code": "NavHost(\n    navController = navController,\n    startDestination = \"home\"\n) {\n    composable(\"home\") { HomeScreen { navController.navigate(\"detail/42\") } }\n    composable(\n        route = \"detail/{id}\",\n        arguments = listOf(navArgument(\"id\") { type = NavType.IntType })\n    ) { entry ->\n        val id = entry.arguments?.getInt(\"id\") ?: 0\n        DetailScreen(id, onBack = { navController.popBackStack() })\n    }\n}\n\n// Bottom bar sync\nval backStackEntry by navController.currentBackStackEntryAsState()\nval currentRoute = backStackEntry?.destination?.route"
        }
      ],
      "dependencies": [
        "androidx.navigation:navigation-compose"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "permissions-runtime",
      "title": "Runtime permissions",
      "description": "Request location (+ camera) permission with the callback flow, and per-app settings.",
      "category": "Other",
      "tags": [
        "permissions",
        "location",
        "manifest"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Android",
          "code": "private val launcher = registerForActivityResult(\n    ActivityResultContracts.RequestMultiplePermissions()\n) { grants ->\n    if (grants.values.all { it }) startLocationTracking()\n    else Toast.makeText(this, \"Location needed to continue\", Toast.LENGTH_LONG).show()\n}\n\nfun requestLocationPermission() {\n    launcher.launch(arrayOf(\n        Manifest.permission.ACCESS_FINE_LOCATION,\n        Manifest.permission.ACCESS_COARSE_LOCATION\n    ))\n}\n\n// If denied twice — open app settings\nfun openAppSettings() {\n    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {\n        data = Uri.parse(\"package:${$}{packageName}\")\n        startActivity(this)\n    }\n}"
        },
        {
          "language": "compose",
          "technology": "Compose",
          "code": "val context = LocalContext.current\nval launcher = rememberLauncherForActivityResult(\n    ActivityResultContracts.RequestPermission()\n) { granted -> state = if (granted) Granted else Denied }\n\nButton(onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {\n    Text(\"Grant location\")\n}\n\n// One-time gate\nif (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)\n        != PackageManager.PERMISSION_GRANTED) {\n    PermissionGate { launcher.launch(Manifest.permission.CAMERA) }\n}"
        }
      ],
      "dependencies": [],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "net-retrofit",
      "title": "Retrofit + OkHttp",
      "description": "Retrofit service + converter, interceptor with auth/headers, coroutine suspend.",
      "category": "Networking",
      "tags": [
        "retrofit",
        "http",
        "api"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Retrofit",
          "code": "// model\n@Serializable data class User(val id: Int, val name: String)\n\n// service\ninterface ApiService {\n    @GET(\"users\")\n    suspend fun getUsers(): List<User>\n\n    @POST(\"users\") suspend fun createUser(@Body user: User): User\n}\n\n// client\nprivate val client = OkHttpClient.Builder()\n    .addInterceptor { chain ->\n        chain.proceed(\n            chain.request().newBuilder()\n                .header(\"Authorization\", \"Bearer ${$}token\")\n                .header(\"Accept\", \"application/json\")\n                .build()\n        )\n    }\n    .build()\n\nval api: ApiService = Retrofit.Builder()\n    .baseUrl(BuildConfig.API_URL)\n    .client(client)\n    .addConverterFactory(jsonConverterFactory)\n    .build()\n    .create(ApiService::class.java)\n\n// use in ViewModel\nval user = api.login(User(1, \"x\"))"
        }
      ],
      "dependencies": [
        "com.squareup.retrofit2:retrofit"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "net-okhttp",
      "title": "OkHttp GET + multipart",
      "description": "Plain OkHttp: async GET, file upload via RequestBody.",
      "category": "Networking",
      "tags": [
        "http",
        "upload",
        "okhttp"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "OkHttp",
          "code": "val client = OkHttpClient()\n\n// GET\nval request = Request.Builder().url(\"https://api.example.com/items\").build()\nclient.newCall(request).enqueue(object : Callback {\n    override fun onFailure(call: Call, e: IOException) {\n        runOnUiThread { showError(e.message) }\n    }\n    override fun onResponse(call: Call, res: Response) {\n        val body = res.body?.string() ?: return\n        runOnUiThread { parseAndShow(body) }\n    }\n})\n\n// Multipart upload\nval multipart = MultipartBody.Builder().setType(MultipartBody.FORM)\n    .addFormDataPart(\"title\", \"Cat pic\")\n    .addFormDataPart(\n        \"file\",\n        file.name,\n        file.asRequestBody(\"image/jpeg\".toMediaType())\n    )\n    .build()\nval upload = Request.Builder()\n    .url(\"https://api.example.com/upload\")\n    .post(multipart)\n    .build()"
        }
      ],
      "dependencies": [
        "com.squareup.okhttp3:okhttp"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "net-ktor",
      "title": "Ktor client",
      "description": "Ktor client with ContentNegotiation — suspend-friendly default for coroutines apps.",
      "category": "Networking",
      "tags": [
        "ktor",
        "http"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Ktor",
          "code": "val client = HttpClient(CIO) {\n    install(ContentNegotiation) {\n        json(Json { ignoreUnknownKeys = true })\n    }\n    install(Logging) { level = LogLevel.BODY }\n    defaultRequest {\n        header(\"Authorization\", \"Bearer <token>\")\n        contentType(ContentType.Application.Json)\n    }\n}\n\n// suspend — call from your ViewModel\nval users: List<User> = client.get(\"https://api.example.com/users\").body()\n\nclient.post(\"https://api.example.com/auth\") {\n    setBody(AuthRequest(\"u\", \"p\"))\n}\n\nclient.close() // end of lifetime"
        }
      ],
      "dependencies": [
        "io.ktor:ktor-client-core"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "db-room",
      "title": "Room",
      "description": "Entity + DAO + database + Flow — Room full stack.",
      "category": "Database",
      "tags": [
        "room",
        "database",
        "flow"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Room",
          "code": "@Entity(tableName = \"todos\")\ndata class Todo(\n    @PrimaryKey(autoGenerate = true) val id: Long = 0,\n    val title: String,\n    val done: Boolean = false\n)\n\n@Dao\ninterface TodoDao {\n    @Query(\"SELECT * FROM todos ORDER BY id DESC\")\n    fun observeAll(): Flow<List<Todo>>\n\n    @Insert suspend fun insert(todo: Todo)\n\n    @Update suspend fun update(todo: Todo)\n\n    @Query(\"DELETE FROM todos WHERE id = :id\") suspend fun delete(id: Long)\n}\n\n@Database(entities = [Todo::class], version = 1)\nabstract class AppDb : RoomDatabase() {\n    abstract fun todoDao(): TodoDao\n}\n\n// Build (singleton)\nval db = Room.databaseBuilder(context, AppDb::class.java, \"app.db\")\n    .fallbackTooDestinationDestructiveMigration()\n    .build()\n\n// Use\nviewModelScope.launch { db.todoDao().insert(Todo(title = \"Ship DevHub\")) }"
        }
      ],
      "dependencies": [
        "androidx.room:room-runtime"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "db-datastore",
      "title": "DataStore Preferences",
      "description": "Typed preferences with coroutines — the modern SharedPreferences.",
      "category": "Database",
      "tags": [
        "datastore",
        "sharedprefs"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Jetpack DataStore",
          "code": "val Context.dataStore by preferencesDataStore(name = \"settings\")\n\n// read — collect Flow\nval darkMode: Flow<Boolean> = context.dataStore.data.map { p ->\n    p[PreferencesKeys.darkMode] ?: false\n}\n\n// write\nsuspend fun setDarkMode(enabled: Boolean) {\n    context.dataStore.edit { it[PreferencesKeys.darkMode] = enabled }\n}\n\nsuspend fun setUsername(name: String) {\n    context.dataStore.edit { it[PreferencesKeys.username] = name }\n}\n\nprivate object PreferencesKeys {\n    val darkMode = booleanPreferencesKey(\"dark_mode\")\n}"
        }
      ],
      "dependencies": [
        "androidx.datastore:datastore-preferences"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "db-sqlite",
      "title": "SQLite (raw)",
      "description": "SQLiteOpenHelper + create Cursorless queries.",
      "category": "Database",
      "tags": [
        "sqlite",
        "database"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "SQLite",
          "code": "class NotesDb private constructor(ctx: Context) : SQLiteOpenHelper(ctx, \"notes.db\", null, 1) {\n    companion object {\n        fun get(ctx: Context) = NoteDbSingleton(ctx)\n    }\n    override fun onCreate(db: SQLiteDatabase) {\n        db.execSQL(\"CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, body TEXT NOT NULL)\")\n    }\n    fun insert(body: String) {\n        val v = ContentValues().apply { put(\"body\", body) };\n        writableDatabase.insert(\"notes\", null, v)\n    }\n    fun all(): List<Map<String, Any?>> {\n        val rows = mutableListOf<Map<String, Any?>>()\n        readableDatabase.query(\"notes\", null, null, null, null, null, \"id DESC\")\n            .use { c -> while (c.moveToNext()) {\n                rows += mapOf(\"id\" to c.getLong(0), \"body\" to c.getString(1))\n            } }\n        return rows\n    }\n}"
        },
        {
          "language": "java",
          "technology": "SQLite",
          "code": "public class NoteDb extends SQLiteOpenHelper {\n    private static final String SQL_CREATE =\n        \"CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, body TEXT NOT NULL)\";\n\n    public NoteDb(Context c) { super(c, \"notes.db\", null, 1); }\n\n    @Override public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE); }\n    @Override public void onUpgrade(SQLiteDatabase db, int o, int n) {\n        db.execSQL(\"DROP TABLE IF EXISTS notes\");\n        onCreate(db);\n    }\n\n    public void insert(String body) {\n        ContentValues v = new ContentValues();\n        v.put(\"body\", body);\n        getWritableDatabase().insert(\"notes\", null, v);\n    }\n}"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "fb-firestore",
      "title": "Firebase — Firestore + Auth",
      "description": "Firestore CRUD (set/get/listen) + email auth, minimal.",
      "tags": [
        "firebase",
        "firestore",
        "auth",
        "boilerplate"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Firebase",
          "code": "// add firebase-common, firestore, auth to dependencies\n\n// Firestore write\nval doc = db.collection(\"users\").document(uid)\ndoc.set(mapOf(\"name\" to \"Ada\", \"level\" to 7))\n\n// Firestore read (reactive)\ndb.collection(\"users\")\n    .whereEqualTo(\"level\", 7)\n    .get()\n    .addOnSuccessListener { snap ->\n        snap.documents.forEach { d -> println(\"${$}{d.id} => ${$}{d.data}\") }\n    }\n\n// Realtime listener\ndb.collection(\"todos\").addSnapshotListener { snap, e ->\n    if (e != null) { Log.w(TAG, e) ; return@addSnapshotListener }\n    // rebuild your list\n}\n\n// Auth\nFirebaseAuth.getInstance()\n    .createUserWithEmailAndPassword(\"a@b.co\", \"secret\")\n    .addOnSuccessListener { result -> result.user?.uid }\n"
        }
      ],
      "dependencies": [
        "com.google.firebase:firebase-firestore"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000,
      "category": "Networking"
    },
    {
      "id": "net-websocket",
      "title": "WebSockets",
      "description": "OkHttp WebSocket client — connect, send, receive, close; reconnect tips.",
      "category": "Networking",
      "tags": [
        "websocket",
        "realtime",
        "okhttp"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "OkHttp",
          "code": "val client = OkHttpClient()\nval wsListener = object : WebSocketListener() {\n    override fun onOpen(webSocket: WebSocket, response: Response) {\n        webSocket.send(\"{\\\"type\\\":\\\"hello\\\"}\")\n    }\n    override fun onMessage(webSocket: WebSocket, text: String) {\n        runOnUiThread { onMessageFromServer(text) }\n    }\n    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {\n        // schedule reconnect with backoff\n    }\n    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) { }\n}\n\nval ws: WebSocket = client.newWebSocket(\n    Request.Builder().url(\"wss://echo.websocket.events/\").build(),\n    wsListener\n)\nws.send(\"ping\")   // send anytime\nws.close(1000, \"bye\") // graceful close\n\nclient.dispatcher.executorService.shutdown() // at app exit"
        }
      ],
      "dependencies": [
        "com.squareup.okhttp3:okhttp"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "bg-workmanager",
      "title": "WorkManager",
      "description": "Long-running background work: app-level unique worker, chain, periodic.",
      "category": "Other",
      "tags": [
        "workmanager",
        "background"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "WorkManager",
          "code": "class UploadWorker(context: Context, params: WorkerParameters) :\n    CoroutineWorker(context, params) {\n\n    override suspend fun doWork(): Result {\n        val url = inputData.getString(\"url\") ?: return Result.failure()\n        return try {\n            api.upload(url)\n            Result.success()\n        } catch (e: Exception) {\n            if (runAttemptCount < 3) Result.retry() else Result.failure()\n        }\n    }\n}\n\n// enqueue\nval request = OneTimeWorkRequestBuilder<UploadWorker>()\n    .setInputData(workDataOf(\"url\" to fileUrl))\n    .setConstraints(Constraints.Builder()\n        .setRequiredNetworkType(NetworkType.CONNECTED)\n        .build())\n    .build()\nWorkManager.getInstance(context).enqueue(request)\n\n// observe with Flow\nWorkManager.getInstance(context)\n    .getWorkInfosForUniqueWork(\"sync\")\n    .asFlow() // observeAsState per screen\n\n// periodic (15 min min)\nval periodic = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES).build()\nWorkManager.getInstance(context).enqueueUniquePeriodicWork(\n    \"sync\", ExistingPeriodicWorkPolicy.KEEP, periodic\n)"
        }
      ],
      "dependencies": [
        "androidx.appcompat:appcompat"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "hilt-simple",
      "title": "Hilt — DI",
      "description": "Module, provide (Repository), inject into Activity + ViewModel, Application class.",
      "category": "Other",
      "tags": [
        "hilt",
        "dependency injection"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Hilt",
          "code": "// App\n@HiltAndroidApp\nclass App : Application()\n\n// provides\n@Module @InstallIn(SingletonComponent::class)\nobject NetworkModule {\n    @Provides @Singleton\n    fun okHttpClient(): OkHttpClient = OkHttpClient.Builder().build()\n}\n\n@Module\ninterface StoreModule {\n    @Binds @Singleton\n    abstract fun store(bind: UserStoreImpl): UserStore\n}\n\n// Consumer\n@AndroidEntryPoint\nclass MainActivity : AppCompatActivity() {\n    @Inject lateinit var store: UserStore\n    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n        store.load()\n    }\n}\n\n// ViewModel\n@HiltViewModel class HomeViewModel @Inject constructor(\n    private val store: UserStore\n) : BaseViewModel()"
        }
      ],
      "dependencies": [
        "com.google.dagger:hilt-android"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "utility-toast",
      "title": "Toast / Snackbar",
      "description": "One-liner toast and M3 snackbar host.",
      "category": "Other",
      "tags": [
        "toast",
        "snackbar",
        "feedback"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Android",
          "code": "Toast.makeText(this, \"Hello\", Toast.LENGTH_SHORT).show()\n\n// custom gravity\nToast.makeText(context, \"Top\", Toast.LENGTH_SHORT).apply {\n    setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)\n    show()\n}"
        },
        {
          "language": "compose",
          "technology": "Compose",
          "code": "val snackbarHostState = remember { SnackbarHostState() }\nval scope = rememberCoroutineScope()\n\nSnackbarHost(hostState = snackbarHostState)\n\n// show\nscope.launch {\n    snackbarHostState.showSnackbar(\n        message = \"Copied!\",\n        actionLabel = \"OK\",\n        duration = SnackbarDuration.Short\n    )\n}\n\n// with FloatingActionButton\nScaffold(\n    snackbarHost = { SnackbarHost(snackbarHostState) },\n    floatingActionButton = {\n        FAB(onClick = { scope.launch { snackbarHostState.showSnackbar(\"Saved\") } })\n    }\n) { padding -> Content(padding) }"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "utility-share",
      "title": "Share / Intents",
      "description": "Share text, open URL, deep-link, send email — quick ACTION intents.",
      "category": "Other",
      "tags": [
        "intent",
        "share",
        "deeplink"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Android",
          "code": "// Share text\nval sendIntent = Intent().apply {\n    action = Intent.ACTION_SEND\n    type = \"text/plain\"\n    putExtra(Intent.EXTRA_SUBJECT, \"Bring it\")\n    text = \"Check out this (code)\"\n}\nstartActivity(Intent.createChooser(send, \"Share via\"))\n\n// Open a website\nstartActivity(Intent(Intent.ACTION_VIEW, Uri.parse(\"https://example.com\")))\n\n// Open a deep link into your own app\nstartActivity(Intent(Intent.ACTION_VIEW, Uri.parse(\"myapp://section/id/42\")))\n\n// Email\nval email = Intent(Intent.ACTION_SENDTO, Uri.parse(\"mailto:hi@example.com\")).apply {\n    putExtra(Intent.EXTRA_SUBJECT, \"Feedback\")\n    putExtra(Intent.EXTRA_TEXT, \"...\")\n}\nstartActivity(email)"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "utility-settings",
      "title": "PreferenceScreen",
      "description": "Preference fragments with categories; jetpack preferences-fast.",
      "category": "Other",
      "tags": [
        "preferences"
      ],
      "implementations": [
        {
          "language": "xml",
          "technology": "Android",
          "code": "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<PreferenceScreen xmlns:android=\"http://schemas.android.com/apk/res/android\">\n    <PreferenceCategory android:title=\"General\">\n        <SwitchPreferenceCompat\n            android:key=\"dark_mode\"\n            android:title=\"Dark mode\"\n            android:summary=\"Use the dark theme\" />\n        <EditTextPreference\n            android:key=\"name\"\n            android:title=\"Display name\" />\n        <ListPreference\n            android:key=\"unit\"\n            android:title=\"Units\"\n            android:entries=\"@array/units\"\n            android:entryValues=\"@array/unit_values\"\n            android:defaultValue=\"metric\" />\n    </PreferenceCategory>\n</PreferenceScreen>\n\n<!-- kotlin\nsetPreferencesFromResource(R.xml.settings, key)\nfindPreference<SwitchPreferenceCompat>(\"dark_mode\")?.setOnPreferenceChangeListener { _, v ->\n    sync(v as Boolean); true\n}"
        }
      ],
      "dependencies": [
        "androidx.preference:preference-ktx"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-techcolors",
      "title": "Colors & theme",
      "description": "M3 color schemes (light/dark), dynamic color, and semantic roles usage.",
      "category": "Colors",
      "tags": [
        "theme",
        "colors"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "@Composable\nfun AppTheme(\n    darkTheme: Boolean = isSystemInDarkTheme(),\n    dynamicColor: Boolean = true,\n    content: @Composable () -> Unit\n) {\n    val colorScheme = when {\n        dynamicColor && Build.VERSION.SDK_INT >= 33 -> {\n            val context = LocalContext.current\n            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)\n        }\n        darkTheme -> DarkColors\n        else -> LightColors\n    }\n    MaterialTheme(\n        colorScheme = colorScheme,\n        typography = AppTypography,\n        shapes = AppShapes,\n        content = content\n    )\n}\n\nprivate val LightColors = lightColorScheme(\n    primary = Color(0xFF0B57D2),\n    onPrimary = Color.White,\n    primaryContainer = Color(0xFFD7E3FF),\n    background = Color(0xFFFDFBFF),\n    surfaceVariant = Color(0xFFE1E2EC)\n)\n\n// usage\nMaterialTheme.colorScheme.primary,\nMaterialTheme.colorScheme.surfaceVariant,\nMaterialTheme.colorScheme.errorContainer"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "layout-constraint",
      "title": "ConstraintLayout",
      "description": "Bias, chains, ratio — the flexible XML layout.\n",
      "category": "Layout",
      "tags": [
        "layout",
        "constraint",
        "xml"
      ],
      "implementations": [
        {
          "language": "xml",
          "technology": "Android Views",
          "code": "<androidx.constraintlayout.widget.ConstraintLayout\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"match_parent\">\n\n    <TextView\n        android:id=\"@id/title\"\n        android:layout_width=\"wrap_content\"\n        android:layout_height=\"wrap_content\"\n        android:text=\"Hello\"\n        app:layout_constraintTop_toTopOf=\"parent\"\n        app:layout_constraintStart_toStartOf=\"parent\"\n        app:layout_constraintEnd_toEndOf=\"parent\" />\n\n    <Button\n        android:id=\"@+id/next\"\n        android:layout_width=\"0dp\"\n        android:layout_height=\"wrap_content\"\n        android:text=\"Next\"\n        app:layout_constraintStart_toStartOf=\"parent\"\n        app:layout_constraintEnd_toEndOf=\"parent\"\n        app:layout_constraintTop_toBottomOf=\"@id/title\"\n        app:layout_constraintHorizontal_bias=\"0.4\" />\n\n</androidx.constraintlayout.widget.ConstraintLayout>"
        }
      ],
      "dependencies": [
        "androidx.constraintlayout:constraintlayout"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "layout-flexbox",
      "title": "FlowRow / Flexbox",
      "description": "Chips que wrap — FlowRow (Compose) or FlexboxLayout (Views).",
      "category": "Layout",
      "tags": [
        "flowrow",
        "chips"
      ],
      "implementations": [
        {
          "language": "compose",
          "technology": "Compose",
          "code": "FlowRow(\n    horizontalArrangement = Arrangement.spacedBy(8.dp),\n    verticalArrangement = Arrangement.spacedBy(8.dp)\n) {\n    tags.forEach { tag ->\n        AssistChip(\n            onClick = { },\n            label = { Text(tag) },\n            leadingIcon = { Icon(Icons.Default.Circle, tag) }\n        )\n    }\n}"
        },
        {
          "language": "xml",
          "technology": "Android Views",
          "code": "<com.google.android.flexbox.FlexboxLayout\n    android:id=\"@+id/chips\"\n    android:layout_width=\"match_parent\"\n    android:layout_height=\"wrap_content\"\n    app:flexWrap=\"wrap\"\n    app:justifyContent=\"flex_start\">\n    <com.google.android.material.chip.Chip\n        android:layout_width=\"wrap_content\"\n        android:layout_height=\"wrap_content\"\n        android:text=\"Java\" />\n    <com.google.android.material.chip.Chip\n        android:layout_width=\"wrap_content\"\n        android:layout_height=\"wrap_content\"\n        android:text=\"Kotlin\" />\n</com.google.android.material.flexbox.FlexboxLayout>"
        }
      ],
      "dependencies": [
        "com.google.android.material:material"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "ui-snackbarxml",
      "title": "Snackbar (Views)",
      "description": "Material Snackbar with action + anchors FAQ.",
      "category": "UI",
      "tags": [
        "snackbar"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Material",
          "code": "Snackbar.make(\n    findViewById(R.id.coordinator),\n    \"message deleted\",\n    Snackbar.LENGTH_LONG\n).setAction(\"Undo\") {\n    undoDelete()\n}.show()\n\n// With delay + anchor near FAB\nSnackbar.make(binding.root, \"Loading…\", Snackbar.LENGTH_INDEFINITE)\n    .setAnchorView(findViewById(R.id.fab))\n    .show()"
        }
      ],
      "dependencies": [
        "com.google.android.material:material"
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    },
    {
      "id": "arch-viewmodel",
      "title": "ViewModel + State — simplest",
      "description": "StateFlow viewModel skeleton every screen started with.",
      "category": "Other",
      "tags": [
        "viewmodel",
        "architecture"
      ],
      "implementations": [
        {
          "language": "kotlin",
          "technology": "Android",
          "code": "data class UiState(\n    val loading: Boolean = false,\n    val items: List<Item> = emptyList()\n)\n\nclass ItemsViewModel(private val repo: Repo) : ViewModel() {\n\n    private val _uiState = MutableStateFlow(UiState())\n    val uiState: StateFlow<UiState> = _uiState.asStateFlow()\n\n    init {\n        viewModelScope.launch {\n            repo.items().collect { items ->\n                _uiState.value = UiState(items = items)\n            }\n        }\n    }\n\n    fun refresh() = viewModelScope.launch {\n        _uiState.value = _uiState.value.copy(loading = true)\n        try {\n            repo.refresh()\n        } finally {\n            _uiState.value = _uiState.value.copy(loading = false)\n        }\n    }\n}\n\n// collectAsStateWithLifecycle(uiState)"
        }
      ],
      "createdAt": 1754000000000,
      "updatedAt": 1754000000000
    }
  ],
  "dependencies": [
    {
      "id": "com.google.android.material:material",
      "groupId": "com.google.android.material",
      "artifactId": "material",
      "latestVersion": "1.12.0",
      "versions": [
        "1.11.0",
        "1.12.0"
      ],
      "repository": "Google Maven",
      "license": "Apache-2.0",
      "releaseDate": "2024-05-14",
      "updatedAt": 1720000000000
    },
    {
      "id": "androidx.recyclerview:recyclerview",
      "groupId": "androidx.recyclerview",
      "artifactId": "recyclerview",
      "latestVersion": "1.3.2",
      "versions": [
        "1.3.2"
      ],
      "repository": "Google Maven",
      "license": "Apache-2.0"
    },
    {
      "id": "androidx.swiperefreshlayout:swiperefreshlayout",
      "groupId": "androidx.swiperefreshlayout",
      "artifactId": "swiperefreshlayout",
      "latestVersion": "1.1.0",
      "repository": "Google Maven",
      "license": "Apache-2.0"
    },
    {
      "id": "androidx.navigation:navigation-compose",
      "groupId": "androidx.navigation",
      "artifactId": "navigation-compose",
      "latestVersion": "2.9.0",
      "versions": [
        "2.8.9",
        "2.9.0"
      ],
      "repository": "Google Maven",
      "license": "Apache-2.0"
    },
    {
      "id": "com.squareup.retrofit2:retrofit",
      "groupId": "com.squareup.retrofit2",
      "artifactId": "retrofit",
      "latestVersion": "2.11.0",
      "versions": [
        "2.9.0",
        "2.11.0"
      ],
      "repository": "Maven Central",
      "license": "Apache-2.0",
      "releaseDate": "2024-03-01"
    },
    {
      "id": "com.squareup.okhttp3:okhttp",
      "groupId": "com.squareup.okhttp3",
      "artifactId": "okhttp",
      "latestVersion": "4.12.0",
      "versions": [
        "4.11.0",
        "4.12.0"
      ],
      "repository": "Maven Central",
      "license": "Apache-2.0"
    },
    {
      "id": "io.ktor:ktor-client-core",
      "groupId": "io.ktor",
      "artifactId": "ktor-client-core",
      "latestVersion": "3.1.0",
      "versions": [
        "2.3.13",
        "3.1.0"
      ],
      "repository": "Maven Central",
      "license": "Apache-2.0"
    },
    {
      "id": "androidx.room:room-runtime",
      "groupId": "androidx.room",
      "artifactId": "room-runtime",
      "latestVersion": "2.7.0",
      "versions": [
        "2.6.1",
        "2.7.0"
      ],
      "repository": "Google Maven",
      "license": "Apache-2.0"
    },
    {
      "id": "androidx.datastore:datastore-preferences",
      "groupId": "androidx.datastore",
      "artifactId": "datastore-preferences",
      "latestVersion": "1.1.4",
      "versions": [
        "1.1.1",
        "1.1.4"
      ],
      "repository": "Google Maven",
      "license": "Apache-2.0"
    },
    {
      "id": "com.google.firebase:firebase-firestore",
      "groupId": "com.google.firebase",
      "artifactId": "firebase-firestore",
      "latestVersion": "25.9.0",
      "versions": [
        "25.8.0",
        "25.9.0"
      ],
      "repository": "Google Maven",
      "license": "Apache-2.0"
    },
    {
      "id": "com.google.dagger:hilt-android",
      "groupId": "com.google.dagger",
      "artifactId": "hilt-android",
      "latestVersion": "2.55",
      "versions": [
        "2.51.1",
        "2.55"
      ],
      "repository": "Maven Central",
      "license": "Apache-2.0"
    },
    {
      "id": "androidx.preference:preference-ktx",
      "groupId": "androidx.preference",
      "artifactId": "preference-ktx",
      "latestVersion": "1.2.1",
      "repository": "Google Maven",
      "license": "Apache-2.0"
    },
    {
      "id": "androidx.constraintlayout:constraintlayout",
      "groupId": "androidx.constraintlayout",
      "artifactId": "constraintlayout",
      "latestVersion": "2.2.1",
      "versions": [
        "2.1.4",
        "2.2.1"
      ],
      "repository": "Google Maven",
      "license": "Apache-2.0"
    }
  ],
  "blocks": [
    {
      "id": "blk-lc",
      "name": "LazyColumn",
      "description": "A scrollable vertical list with items()",
      "languages": [
        "kotlin"
      ],
      "trigger": "lc",
      "template": "LazyColumn(\n    modifier = Modifier.${$}{1:modifier},\n) {\n    items(${$}{2:items}) { ${$}{3:item} ->\n        ${$}{0}\n    }\n}\n",
      "createdAt": 1786241975655,
      "updatedAt": 1786241975655
    },
    {
      "id": "blk-mkv",
      "name": "mutableStateOf + remember",
      "description": "Compose state that survives recomposition",
      "languages": [
        "kotlin"
      ],
      "trigger": "mkv",
      "template": "val ${$}{1:name} = remember { mutableStateOf(${$}{2:initial}) }",
      "createdAt": 1786241975655,
      "updatedAt": 1786241975655
    },
    {
      "id": "blk-rs",
      "name": "rememberSaveable state",
      "description": "State that survives activity recreation",
      "languages": [
        "kotlin"
      ],
      "trigger": "rs",
      "template": "val ${$}{1:name} = rememberSaveable { mutableStateOf(${$}{2:initial}) }",
      "createdAt": 1786241975655,
      "updatedAt": 1786241975655
    },
    {
      "id": "blk-sc",
      "name": "rememberCoroutineScope",
      "description": "A scope to launch coroutines inside a composable",
      "languages": [
        "kotlin"
      ],
      "trigger": "cscope",
      "template": "val ${$}{1:scope} = rememberCoroutineScope()",
      "createdAt": 1786241975655,
      "updatedAt": 1786241975655
    },
    {
      "id": "blk-tx",
      "name": "Text composable",
      "description": "A Text with a style placeholder",
      "languages": [
        "kotlin"
      ],
      "trigger": "txt",
      "template": "Text(\n    text = ${$}{1:text},\n    style = ${$}{2:style},\n)",
      "createdAt": 1786241975655,
      "updatedAt": 1786241975655
    },
    {
      "id": "blk-fori",
      "name": "indexed for loop",
      "description": "for (int i = 0; …); the counter is one linked placeholder",
      "languages": [
        "java"
      ],
      "trigger": "fori",
      "template": "for (int ${$}{1:i} = 0; ${$}1 < ${$}{2:n}; ${$}1++) {\n    ${$}{0}\n}\n",
      "createdAt": 1786241975655,
      "updatedAt": 1786241975655
    },
    {
      "id": "blk-fl",
      "name": "enhanced for loop",
      "description": "foreach over a collection",
      "languages": [
        "java"
      ],
      "trigger": "fore",
      "template": "for (${$}{1:type} ${$}{2:e} : ${$}{3:items}) {\n    ${$}{0}\n}\n",
      "createdAt": 1786241975655,
      "updatedAt": 1786241975655
    }
  ]
}
"""
