# 07 — Couche réseau (Retrofit)

## 7.1 Rôle

La couche réseau simule l'**envoi d'une alerte à un backend**. Elle utilise **Retrofit 2** (client HTTP type-safe) avec le convertisseur **Gson** (JSON ↔ objets Java).

`BASE_URL = "https://jsonplaceholder.typicode.com/"` — une API publique de test qui accepte les requêtes et renvoie une réponse réaliste, sans rien stocker durablement. Elle remplace un vrai backend pour la démonstration.

## 7.2 Le client singleton (`RetrofitClient`)

```java
public class RetrofitClient {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)                       // doit finir par "/"
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
    public static RetrofitClient getInstance() { … }  // 1 seule instance
    public ApiService getApiService() { return retrofit.create(ApiService.class); }
}
```

**Pourquoi un singleton ?** Construire un objet `Retrofit` est coûteux. On le crée **une seule fois** et on réutilise l'instance — jamais à chaque clic.

## 7.3 L'interface d'API (`ApiService`)

```java
public interface ApiService {
    @POST("posts")
    Call<AlerteApi> envoyerAlerte(@Body AlerteApi alerte);

    @GET("posts")
    Call<List<AlerteApi>> getAlertes();
}
```

- `@POST("posts")` : envoie une alerte (le corps JSON est l'objet `AlerteApi` sérialisé par Gson).
- `@GET("posts")` : récupèrerait la liste des alertes côté serveur.

## 7.4 Appel asynchrone (`enqueue`)

L'envoi se fait dans `NouvelleAlerteActivity`, **toujours en asynchrone** avec `enqueue()` (jamais `execute()`, qui bloquerait le thread principal) :

```java
retrofitClient.getApiService().envoyerAlerte(alerteApi).enqueue(new Callback<AlerteApi>() {
    @Override public void onResponse(Call<AlerteApi> call, Response<AlerteApi> response) {
        if (response.isSuccessful() && response.body() != null) {
            // succès réseau
        }
        sauvegarderEtTerminer(alerte);     // ✅ sauvegarde SQLite quoi qu'il arrive
    }
    @Override public void onFailure(Call<AlerteApi> call, Throwable t) {
        // échec réseau
        sauvegarderEtTerminer(alerte);     // ✅ sauvegarde SQLite quand même (offline)
    }
});
```

## 7.5 Stratégie offline-first

Point essentiel pour une app d'urgence : **le réseau ne doit jamais empêcher d'enregistrer une alerte**.

| Cas | Comportement |
|-----|--------------|
| `onResponse` (succès) | Toast « Alerte envoyée » + sauvegarde SQLite + notification |
| `onFailure` (pas de réseau) | Toast « Erreur réseau, alerte sauvegardée localement » + sauvegarde SQLite + notification |

Dans les **deux cas**, l'alerte est persistée localement et l'utilisateur est notifié. L'alerte apparaît ensuite dans l'onglet Alertes.

## 7.6 Dépendances (build.gradle)

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
```

Et la permission `android.permission.INTERNET` dans le manifest.

## 7.7 Évolution vers un vrai backend

Pour passer à un backend réel :
1. Remplacer `BASE_URL` par l'URL de votre API.
2. Adapter `AlerteApi` au schéma JSON attendu (ou créer de nouveaux POJO).
3. Ajouter les en-têtes/authentification si besoin (ex. token JWT via un `Interceptor` OkHttp).
4. La logique d'appel (`enqueue`, offline-first) reste identique.
