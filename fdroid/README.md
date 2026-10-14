# F-Droid

A app está publicada no F-Droid. A receita de build em uso está em
[fdroiddata: metadata/app.sopadeletras.yml](https://gitlab.com/fdroid/fdroiddata/-/blob/master/metadata/app.sopadeletras.yml).
A descrição, o ícone, os screenshots e os changelogs vêm de `fastlane/metadata/android/`.

Para lançar uma nova versão:

1. Atualiza `versionName` e `versionCode` em `app/build.gradle.kts`.
2. Acrescenta `fastlane/metadata/android/<idioma>/changelogs/<versionCode>.txt` (pt-PT e en-US).
3. Faz commit e cria a tag `v<versionName>`. A CI publica o APK assinado na release do GitHub.
4. O F-Droid deteta a tag, compila a app e confirma que o build reproduz o APK da release.
