# F-Droid

`app.sopadeletras.yml` é o rascunho do ficheiro de metadados para o repositório
[fdroiddata](https://gitlab.com/fdroid/fdroiddata). A descrição, os screenshots e os
changelogs vêm de `fastlane/metadata/android/`.

Para submeter: faz fork de `fdroiddata`, copia este ficheiro para `metadata/app.sopadeletras.yml`
e abre um merge request seguindo o [guia de inclusão](https://f-droid.org/docs/Inclusion_How-To/).
Antes de cada nova versão, atualiza `versionName`/`versionCode` em `app/build.gradle.kts` e acrescenta
`fastlane/metadata/android/<idioma>/changelogs/<versionCode>.txt`.
