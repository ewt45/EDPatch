![icon](/patchapp/src/main/res/mipmap-xxhdpi/ic_launcher.png)

🌐[中文](../readme.md)\
🌐[English](./readme_en.md)\
🌐[Русский](./readme_ru.md)


## O проекте
После добавления некоторых небольших функций в exagear (3.0.2) (в основном путем изменения dex) я обнаружил, что вручную изменять smali слишком проблематично.  Даже если предоставлено руководство с указанием измененного местоположения и кодов, оно не подходит для большинства людей, не имеющих опыта модификации apk.
Поэтому мне интересно, могу ли я создать приложение.  Пользователю нужно только нажать кнопку и дождаться завершения автоматической модификации, а затем установить новый apk.  Так родилось это приложение.  Из-за моих ограниченных знаний 100% успех модификации или совместимость со всеми версиями exagear не гарантируется.

- Видео: [Youtube](https://youtu.be/t0y_AcWhZxI), [哔哩哔哩](https://www.bilibili.com/video/BV1mY411X7Nn/)
- Скачать: [release](https://github.com/ewt45/EDPatch/releases)
## Применение
### Действия
1. Выберите apk exagear из списка установленных приложений или выберите локальных файл apk. Дождитесь завершения декодирования.
2. Отметьте функции, которые хотите добавить. Описание функций приведено ниже.
3. Нажмите кнопку Запуск исправлений и терпеливо ожидайте завершения сборки. Информацию о процессе можно увидеть в логе.
4. После сборки apk нажмите кнопку Установить исправленный apk, чтобы установить его.

### Внимание
1. Этот apk является экспериментальным, и успешное добавление функции не гарантируется.
2. После нажатия кнопки Запуск исправлений, пожалуйста, не нажимайте другие кнопки и не сворачивайте приложение, иначе могут возникнуть проблемы.
3. Исправленный apk находится в /storage/emulated/0/Android/data/com.ewt45.patchapp/files/patchtmp/tmp/dist/signed/tmp_sign.apk. Вы можете проверить это вручную с помощью любого файлового менеджера.
4. Если опция "Использовать подпись по умолчанию" в настройках не отмечена, после исправления вам необходимо сначала удалить существующие apk, если используют то же имя пакета или тот же sharedUserId (оверлей ExaGear и VirGL). Если вам нужен внеший оверлей VirGL, подпишите их с той же подписью вручную.

## функций
- [Кнопка настроек](https://ewt45.github.io/blogs/2022/winter/exagearFab/) 
  - Выбор локации диска D
  - Кастомное управление
  - PulseAudio (XSDL)
  - Virgl Overlay (only enabled for apk with old VO view(OverlayBuildUI) added)
  - Xegw
- [Отображать курсор](https://ewt45.github.io/blogs/2022/winter/exagearDefaultCursor/)
- [Настройки контейнера - Кастомное разрешение](https://ewt45.github.io/blogs/2022/autumn/exagearCustomResl/)
- [Исправление работы клавиатуры на android 11+](https://ewt45.github.io/blogs/2022/autumn/exagearKeyboard/)
- [Выбор файла obb](https://ewt45.github.io/blogs/2022/winter/exagearFindObb/)
- Добавить ярлыки .exe на рабочий стол Android
- MultiWine v2
- Настройки контейнера - Выбор рендера
- Настройки контейнера - Дополнительные аргументы

## Third-party project dependencies
- [apktool](https://ibotpeaches.github.io/Apktool/)
- [common-io](https://commons.apache.org/proper/commons-io/)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [apksig](https://android.googlesource.com/platform/tools/apksig)
- [AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)
- [Gson](https://github.com/google/gson)
- [org.tukaani.xz](https://tukaani.org/xz/)




## Change Log


### v1.0.0
- **EDPatch brand new UI**
  - The three steps are divided into separate screens, tap FAB in the upper right corner to go to the next step, and press the phone's back button to return to the previous step.
  - The log interface can be displayed by scrolling down from the title position.
  - Use RecyclerView to display function list, add split line, items will be in two columns on wide screen, tap an item to see function introduction, functions now support displaying function version number.
  - When selecting apk from Installed Apps, results sorting ignores upper/lower case and displays app icon.
  - Guide - function introduction layout scrolls more smoothly.
- **Old functions updated**:
  1. **FAB - PulseAudio**: work directory and log directory changed to z:/opt/edpatch/pulseaudio-xsdl
  2. **Container Settings** - Renderer: txt location changed to /opt/edpatch/renderers.txt
  3. **Custom Controls**:
     - Absolute position click: 1st finger long press, 2nd finger press = mouse right button long press
     - Relative position click: 2nd finger = right click now can be triggered more easily.
- **New functions available**:
  1. **FAB - Virgl Overlay**: this feature is only enabled for apk with old VO view(OverlayBuildUI) added. It will remove the old view inserted at the top of content view. 
  2. **Настройки контейнера - Дополнительные аргументы**
     - Эти аргументы будут добавлены в основную команду запуска контейнера.  Полную команду можно найти в файле по пути /sdcard/x86-stderr.txt.
     - Если аргумент уже включен в исходную команду, добавление аргумента может быть неудачным.  Фактический результат можно просмотреть, нажав кнопку "Предварительный просмотр", используйте её чтобы ввести тестовую команду аргумента, или для просмотра аргумента в текстовом формате. 
     - Все объявленные аргументы хранятся в Z:/opt/edpatch/contArgs.txt, а параметры, включенные для каждого контейнера, хранятся в Z:/home/xdroid_n/contArgs.txt.  Однако напрямую изменять txt не рекомендуется из-за строгих требований к формату хранения.
     - Тип аргумента - ENV или CMD.
       - Переменная среды: используется для выполняемой команды. Если исходный cmd содержит окружение с тем же именем, эти параметры будут переопределены.
       - Cmd: Если он находится до или после исходного cmd, этот cmd и исходный cmd соединяются одним знаком '&'.


### v0.1.1
- Old functions updated:
1. select obb manually
- fix: clicking the blank space leads to an error of tmp.obb not found.
- support bundled obb file (place it at `apk/assets/obb/*.obb` or `apk/lib/armeabi-v7a/libres.so`)
2. Custom location of drive D
- Name changed: `Change Locations of Drives`
- able to add multiple drives. All containers use the same config, config file saved at z:/opt/drives.txt
- able to display more that one external device, named as "other storage device - (1,2...)". The old name "Sd card" is not accurate. Root folder is now added as an option for external devices.
3. Custom Control
- fix: When editing one column of Left&Right side layout, reselect keys result in reset of key's order.

- Others: fix: custom resolution function doesn't read the installed version number correctly.

### v0.1.0
- Old functions updated:
1. container settings - renderers
   - fix: if lines start with # appears in the middle of txt, the lines below will not be read correctly.
   - txt supports adding undefined renderers.(copy a renderer lines, change its key, name and env) after added it will be shown in the options.
2. app shortcut
   - support icons. (if any)
3. show cursor
   - fix: in the last version(v0.0.6) assets/mouse.png is missing

### v0.0.6
- New functions available:
1. Xegw: In the gear button. Provide -legacy-drawing param to solve the problem of only a black screen with an arrow mouse displaying.

- Old functions updated:
1. pulseaudio: keep .config/pulse/deamon.conf while deleting other cache files.
2. Custom Control
- First-person viewport movement: Renamed to Restrict mouse movement. Two options are shown when checked: Position-update interval and Limited distance. New mouse movement option for joystick buttons. Touchpad mode is also adjusted to limit or not the movement distance according to the settings.
- Fix the problem of transparency lost when button is in long-press mode.
3. container settings - renderers:In renderer.txt, 'path' lines are changed to 'env' lines, so that more custom env params can be added by user，After upgrading this function, it is recommended to delete /opt/renderers.txt and then open the container settings once to see the formatting in the automatically generated txt.

- Others:
1. Disable Install button if there's no patched apk.

### v0.0.5
- New functions available:
1. PulseAudio (XSDL): PulseAudio используется для воспроизведения звука, уменьшения проблемы связанных со звуком. Эта функция использует сервер PulseAudio, извлеченный из apk Xserver XSDL. Что требуется поддержка 64-битной версии Андроид на вашем девайсе.

- Old functions updated:
1. Кнопка настроек:
  - Long press to hide.
  - export logcat logs: If there's a folder named logcat in drive d, the logs will be saved inside this folder. Helpful when debugging.
2. Настройки контейнера - Выбор рендера:
   - Раньше Multiwine v1 поддерживал по умолчанию использование разных рендеров для каждого контейнера в v2 этого больше нет. Если вы хотите продолжать использовать разные рендеры для каждого контейнера в MultiWine v2 вам нужно исправить ее вручную.
   - Установите разные LD_LIBRARY_PATH для разных рендеров. libGL.so.1 будет загружаться с выбранного пути с более высоким приоритетом. Вы можете редактировать пути самостоятельно в файле Z:/opt/renderers.txt.
   - Некоторые параметры рендера имеют дополнительные опции при загрузке:
     - Не работает рендер Turnip: Перенаправьте VK_ICD_FILENAMES в несуществующий файл, этот параметр на случай, если Turnip рендеры не будут работать.
     - Встроенный VirGl: запуск libvirgl_test_server.so в новом java-процессе. Работает только в apk  с поддержкой xegw. Больше нет необходимости в Mcat и /opt/start.sh.
       Лог при этом выводится в Android/data/packageName/logs/virglLog.txt.
     - VirtIO-GPU: запускается через Mcat. До Xegw Mcat использовался для запуска среды proot для этого рендера, но в xegw он переписан для запуска /opt/start.sh и не запускает proot автоматически.

- Others:
  - apk size shrunk.

### v0.0.4

- MultiWine v2: Добавляется значок загрузки необходимой версии wine в левом верхнем углу страницы "создание контейнеров", тапните на вкладку "добавление/удаление wine".
  - Установка wine: Вы можете редактировать загруженные или предустановленные версии wine. После выбора опции Установить (Извлечь) выбранная версия wine будет отображаться как 'Активная', теперь её можно выбрать при создании нового контейнера. Используйте опцию Удалить (удалить папку с wine), чтобы уменьшить объем занятой внутренней памяти вашего девайса. Файлы wine хранятся в папке Z:/opt/WineCollection.
  - Загрузка файлов wine: загрузка всех видов wine из интернета. Доступные источники: WineHQ (официальная сборка, перечислены только сборки ubuntu18) и Kron4ek (уменьшенный размер, промежуточные версии wine не включены). Загруженные версии wine появляются на странице 'Установленные'.
  - Как добавить предустановленные версии wine в кэши: `/opt/WineCollection/custom/$TagFolder/$WineFolder/bin/wine`
    - `$TagFolder` : имя папки необходимого wine, оно отображается при создании контейнеров, и должно быть уникальным.
    - `$WineFolder`: он должен содержать двоичный файл `./bin/wine`.
    - так же в `$TagFolder` должен быть `.tar.xz` архивный файл, из которого будет извлечена `$WineFolder`.

- Настройки контейнера - Выбор рендера
  - the renderer option is separated from multiwine now. multiwine v2 alone doesn't have the ability to set environment variables by renderer options in container settings. you need to add the function separately from edpatch.
  - what render options do:
    - each renderer has a diffrent path for libraries e.g.libGL.so.1, this path will be added as LD_LIBRARY_PATH=xxx so that you don't need to replace it at /usr/lib/i386-linux-gnu everytime. If you want to change this path, go to apk's dex -> ContainerSettingsFragment$renderEntries.smali
    - virgl overlay: add VTEST_WIN=1  VTEST_SOCK=
    - VirGL_built_in: run libvirgl_test_server.so from java process (without need of Mcat and /opt/start.sh) , logs output at /sdcard/virglLog.txt
    - virtio-gpu: try to start Mcat, which used to launch a proot environment for this renderer, but xegw replaces the old mcat so it won't work unless you put the old mcat.so back.
    - turnip dxvk: add GALLIUM_DRIVER=zink MESA_VK_WSI_DEBUG=sw

### v0.0.3
- Использовать подпись по умолчанию, нет необходимости удалять установленный apk или переподписывать apk вручную. Но он может быть распознан как вирус.
- New functions available: Добавить ярлыки .exe на рабочий стол Android
  - Нажмите и удерживайте значок приложения, выберите ярлык исполняемого файла и нажмите, чтобы запустить его.
  - Как добавить ярлык программы: тап по меню необходимого ярлыка файла на вкладке "Рабочий стол" и выбрать "Добавить как внешний ярлык".
  - Примечание. Можно добавить не более четырех ярлыков. Перед запуском с ярлыка убедитесь, что приложение не работает в фоновом режиме. После удаления ярлыка exe (файл .desktop) внешний ярлык будет автоматически удален при следующем запуске приложения. Для этой функции требуется Android 7 и выше.

- Old functions updated:
  - Отображать курсор: Изображение должно быть сохранено в `Z:/opt/mouse.png` или в `assets/mouse.png` в файле apk. Первое расположение имеет более высокий приоритет.

### v0.0.2
- EDPatch interface and available functions support Russian now, thanks to Ēlochnik.
- Add a new function: Кастомное управление
  - Большинство опций имеют описания, которые появляются при длительном нажатии. Выберите управление Default в настройках контейнера. После входа в контейнер тап тремя пальцами вызывает меню для редактирования.
  - Мышь: переключение видимости курсора мыши.  Переключение режимов жестов (режим по умолчанию и режим сенсорной панели). Установите скорость перемещения мыши.
  - Кнопки: настройка кнопок (клавиши, текст и положение). Переключение режима управления (боковая панель и свободная позиция кнопок). Доступны кнопки мыши и кнопки в стиле джойстика.
  - Стиль: Настройте цвет кнопки, прозрачность, размер, цвет фона боковой панели.
- Old functions updated:
  - Исправление работы клавиатуры на android 11+: Android 11 and above show/hide logic fix, use `toggleSoftInput()` method (In this way the input method can't hide by clicking from the popup menu, not a big problem, the android back key will be able to hide it). Change the time delay of calling out the input method from 1 second to 0.2 seconds.
  - Выбор файла obb: The position of the text prompt is changed. Now after selecting the file, the text will be replaced by `obb selected, decompressing` or `the selected file is not obb`, in case the user selects the right obb without the correct prompt and selects something again. Display the selected file name as a toast. Disable the select button when unpacking.
  - Выбор локации диска D: After the app starts, if there is no preset folder (default is `Exagear`), it will try to create it automatically.
  - Выбор разрешений экрана: Added multi-language support, probably fix the issue that crashing when container_prefs.xml contains a preferenceScreen of an external keyboard link.


### v0.0.1
- First release. Available functions:
  - Кнопка настроек (Выбор локации диска D)
  - show cursor
  - Выбор разрешений экрана
  - Исправление работы клавиатуры на android 11+
  - Выбор файла obb