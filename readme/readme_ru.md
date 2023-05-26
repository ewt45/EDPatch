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
3. Исправленный apk находится в /storage/emulated/0/Android/data/com.ewt45.patchapp/files/patchtmp/tmp/dist/tmp_sign.apk. Вы можете проверить это вручную с помощью любого файлового менеджера.
4. Если опция "Использовать подпись по умолчанию" в настройках не отмечена, после исправления вам необходимо сначала удалить существующие apk, если используют то же имя пакета или тот же sharedUserId (оверлей ExaGear и VirGL). Если вам нужен внеший оверлей VirGL, подпишите их с той же подписью вручную.

## функций
- [Кнопка настроек](https://ewt45.github.io/blogs/2022/winter/exagearFab/) 
  - Выбор локации диска D
  - Кастомное управление
- [Отображать курсор](https://ewt45.github.io/blogs/2022/winter/exagearDefaultCursor/)
- [Выбор разрешений экрана](https://ewt45.github.io/blogs/2022/autumn/exagearCustomResl/)
- [Исправление работы клавиатуры на android 11+](https://ewt45.github.io/blogs/2022/autumn/exagearKeyboard/)
- [Выбор файла obb](https://ewt45.github.io/blogs/2022/winter/exagearFindObb/)
- Добавить ярлыки .exe на рабочий стол Android

## Third-party project dependencies
- [apktool](https://ibotpeaches.github.io/Apktool/)
- [common-io](https://commons.apache.org/proper/commons-io/)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [apksig](https://android.googlesource.com/platform/tools/apksig)
- [AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)





## Change Log

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