// Lingua Space — interface language (UI only; your own entries are not translated)
(function () {
    const STORAGE_KEY = "lingua-space-ui-lang";

    const LANGS = [
        { code: "en", label: "English", locale: "en-US" },
        { code: "ja", label: "日本語", locale: "ja-JP" },
        { code: "es", label: "Español", locale: "es-ES" },
        { code: "ko", label: "한국어", locale: "ko-KR" },
        { code: "vi", label: "Tiếng Việt", locale: "vi-VN" },
        { code: "fr", label: "Français", locale: "fr-FR" },
        { code: "zh", label: "中文", locale: "zh-CN" }
    ];

    // key: { en, ja, es, ko, vi, fr, zh }
    const T = {
        "ui.picker": {
            en: "Interface language", ja: "表示言語", es: "Idioma de la interfaz", ko: "표시 언어",
            vi: "Ngôn ngữ giao diện", fr: "Langue de l'interface", zh: "界面语言"
        },
        "common.delete": {
            en: "Delete", ja: "削除", es: "Eliminar", ko: "삭제", vi: "Xóa", fr: "Supprimer", zh: "删除"
        },

        // Navigation
        "nav.home": {
            en: "Home", ja: "ホーム", es: "Inicio", ko: "홈", vi: "Trang chủ", fr: "Accueil", zh: "首页"
        },
        "nav.languageHome": {
            en: "Language Home", ja: "言語ホーム", es: "Inicio de idiomas", ko: "언어 홈",
            vi: "Trang ngôn ngữ", fr: "Accueil des langues", zh: "语言首页"
        },
        "nav.vocab": {
            en: "My Vocabulary", ja: "マイ単語帳", es: "Mi vocabulario", ko: "나의 단어장",
            vi: "Từ vựng của tôi", fr: "Mon vocabulaire", zh: "我的词汇"
        },
        "nav.reading": {
            en: "Reading & Expression", ja: "読解と表現", es: "Lectura y expresión", ko: "읽기와 표현",
            vi: "Đọc & diễn đạt", fr: "Lecture & expression", zh: "阅读与表达"
        },
        "nav.archive": {
            en: "Learning Archive", ja: "学習アーカイブ", es: "Archivo de aprendizaje", ko: "학습 아카이브",
            vi: "Kho học tập", fr: "Archives d'apprentissage", zh: "学习档案"
        },
        "nav.language": {
            en: "Language", ja: "言語", es: "Idiomas", ko: "언어", vi: "Ngôn ngữ", fr: "Langues", zh: "语言"
        },

        // Home
        "home.workspace": {
            en: "WORKSPACE", ja: "ワークスペース", es: "ESPACIO", ko: "워크스페이스",
            vi: "KHÔNG GIAN", fr: "ESPACE", zh: "工作区"
        },
        "home.eyebrow": {
            en: "YOUR PERSONAL LANGUAGE SPACE", ja: "あなただけの言語スペース",
            es: "TU ESPACIO PERSONAL DE IDIOMAS", ko: "나만의 언어 공간",
            vi: "KHÔNG GIAN NGÔN NGỮ CỦA BẠN", fr: "VOTRE ESPACE LINGUISTIQUE PERSONNEL", zh: "你的个人语言空间"
        },
        "home.title": {
            en: "Gather languages, and grow yourself.", ja: "言葉を集めて、自分を広げる場所。",
            es: "Reúne idiomas y amplía tu mundo.", ko: "언어를 모으고, 나를 넓히는 곳.",
            vi: "Thu thập ngôn ngữ, mở rộng chính mình.", fr: "Rassembler les langues, s'élargir soi-même.",
            zh: "收集语言，拓展自己。"
        },
        "home.date": {
            en: "MY WORKSPACE", ja: "マイワークスペース", es: "MI ESPACIO", ko: "MY WORKSPACE",
            vi: "KHÔNG GIAN CỦA TÔI", fr: "MON ESPACE", zh: "我的工作区"
        },
        "home.welcomeLabel": {
            en: "A LITTLE, EVERY DAY", ja: "毎日すこしずつ", es: "UN POCO CADA DÍA", ko: "매일 조금씩",
            vi: "MỖI NGÀY MỘT CHÚT", fr: "UN PEU, CHAQUE JOUR", zh: "每天一点点"
        },
        "home.welcomeTitle": {
            en: "Discover new expressions today.", ja: "今日も新しい表現に出会いましょう。",
            es: "Descubre nuevas expresiones hoy.", ko: "오늘도 새로운 표현을 만나 보세요.",
            vi: "Hôm nay hãy gặp những cách diễn đạt mới.", fr: "Découvrez de nouvelles expressions aujourd'hui.",
            zh: "今天也来遇见新的表达吧。"
        },
        "home.welcomeBody": {
            en: "From single words to long sentences.<br>A place to learn, record, and make the language your own.",
            ja: "単語ひとつから長い文章まで。<br>学び、記録し、自分だけの言葉にしていく場所です。",
            es: "Desde una palabra hasta frases largas.<br>Un espacio para aprender, anotar y hacer tuyo el idioma.",
            ko: "단어 하나부터 긴 문장까지.<br>배우고, 기록하고, 나만의 언어로 만들어 가는 공간입니다.",
            vi: "Từ một từ đơn lẻ đến câu dài.<br>Nơi bạn học, ghi lại và biến ngôn ngữ thành của riêng mình.",
            fr: "Des mots isolés aux longues phrases.<br>Un lieu pour apprendre, noter et faire de la langue la vôtre.",
            zh: "从单个词到长句。<br>在这里学习、记录，把语言变成属于你自己的东西。"
        },
        "home.myLanguages": {
            en: "My Languages", ja: "マイ言語", es: "Mis idiomas", ko: "나의 언어",
            vi: "Ngôn ngữ của tôi", fr: "Mes langues", zh: "我的语言"
        },
        "home.spaces": {
            en: "4 learning spaces", ja: "4つの学習スペース", es: "4 espacios de aprendizaje", ko: "4개의 학습 공간",
            vi: "4 không gian học", fr: "4 espaces d'apprentissage", zh: "4 个学习空间"
        },
        "home.deskTitle": {
            en: "My Learning Desk", ja: "マイ学習デスク", es: "Mi escritorio de estudio", ko: "나의 학습 책상",
            vi: "Bàn học của tôi", fr: "Mon bureau d'étude", zh: "我的学习桌"
        },
        "home.deskSub": {
            en: "YOUR PERSONAL ARCHIVE", ja: "あなた専用のアーカイブ", es: "TU ARCHIVO PERSONAL",
            ko: "나만의 아카이브", vi: "KHO LƯU CÁ NHÂN", fr: "VOTRE ARCHIVE PERSONNELLE", zh: "你的个人档案"
        },
        "home.deskEmpty": {
            en: "No learning records yet.<br>Words, sentences, and recent study notes you save will appear here.",
            ja: "まだ学習記録がありません。<br>保存した単語や文、最近の学習記録がここに表示されます。",
            es: "Aún no hay registros.<br>Aquí aparecerán las palabras, frases y notas de estudio que guardes.",
            ko: "아직 기록된 학습 내용이 없습니다.<br>앞으로 이곳에 저장한 단어와 문장, 최근 학습 기록을 모아 볼 수 있어요.",
            vi: "Chưa có ghi chép học tập nào.<br>Các từ, câu và ghi chú bạn lưu sẽ hiển thị ở đây.",
            fr: "Aucun enregistrement pour l'instant.<br>Vos mots, phrases et notes d'étude apparaîtront ici.",
            zh: "还没有学习记录。<br>你保存的单词、句子和最近的学习笔记会显示在这里。"
        },

        // Language names (used for the language cards)
        "lang.zh": { en: "Chinese", ja: "中国語", es: "Chino", ko: "중국어", vi: "Tiếng Trung", fr: "Chinois", zh: "中文" },
        "lang.ja": { en: "Japanese", ja: "日本語", es: "Japonés", ko: "일본어", vi: "Tiếng Nhật", fr: "Japonais", zh: "日语" },
        "lang.es": { en: "Spanish", ja: "スペイン語", es: "Español", ko: "스페인어", vi: "Tiếng Tây Ban Nha", fr: "Espagnol", zh: "西班牙语" },
        "lang.ko": { en: "Korean", ja: "韓国語", es: "Coreano", ko: "한국어", vi: "Tiếng Hàn", fr: "Coréen", zh: "韩语" },

        "card.zh.desc": {
            en: "Tones, pronunciation, and natural sentences, step by step.",
            ja: "発音と声調から、自然な文章まで少しずつ。",
            es: "Tonos, pronunciación y frases naturales, paso a paso.",
            ko: "성조와 발음부터 자연스러운 문장까지 차근차근.",
            vi: "Thanh điệu, phát âm và câu tự nhiên, từng bước một.",
            fr: "Tons, prononciation et phrases naturelles, pas à pas.",
            zh: "从声调、发音到自然的句子，循序渐进。"
        },
        "card.ja.desc": {
            en: "Everyday expressions and the nuance of sentences.",
            ja: "日常で使われる表現と文のニュアンスを身につける。",
            es: "Expresiones cotidianas y matices de las frases.",
            ko: "일상에서 쓰이는 표현과 문장의 뉘앙스 익히기.",
            vi: "Cách diễn đạt hằng ngày và sắc thái của câu.",
            fr: "Expressions du quotidien et nuances des phrases.",
            zh: "学习日常表达和句子的细微语气。"
        },
        "card.es.desc": {
            en: "Listen, read, and speak to widen your expressions.",
            ja: "聞いて、読んで、話しながら表現の幅を広げる。",
            es: "Escucha, lee y habla para ampliar tus expresiones.",
            ko: "듣고 읽고 말하면서 표현의 폭 넓히기.",
            vi: "Nghe, đọc và nói để mở rộng cách diễn đạt.",
            fr: "Écouter, lire et parler pour élargir son expression.",
            zh: "通过听、读、说，拓展你的表达。"
        },
        "card.ko.desc": {
            en: "Vocabulary, idioms, sentence skills, and subtle expression.",
            ja: "語彙、慣用句、文章力、繊細な表現を育てる。",
            es: "Vocabulario, modismos, redacción y expresiones sutiles.",
            ko: "어휘, 관용구, 문장력과 섬세한 표현 기르기.",
            vi: "Từ vựng, thành ngữ, kỹ năng viết câu và cách diễn đạt tinh tế.",
            fr: "Vocabulaire, expressions idiomatiques, phrases et nuances.",
            zh: "词汇、惯用语、句子能力与细腻的表达。"
        },

        // Language directory
        "lp.eyebrow": {
            en: "YOUR LANGUAGE DIRECTORY", ja: "あなたの言語ディレクトリ", es: "TU DIRECTORIO DE IDIOMAS",
            ko: "나의 언어 디렉터리", vi: "DANH MỤC NGÔN NGỮ", fr: "VOTRE RÉPERTOIRE DE LANGUES", zh: "你的语言目录"
        },
        "lp.desc": {
            en: "Find the language you want to learn.<br>Pick a tab, or search by name in English or Korean.",
            ja: "学びたい言語を探しましょう。<br>タブで選ぶか、英語や韓国語の名前で検索できます。",
            es: "Busca el idioma que quieres aprender.<br>Elige una pestaña o busca por nombre en inglés o coreano.",
            ko: "배우고 싶은 언어를 찾아보세요.<br>상단 탭으로 언어를 고르거나, 검색창에서 한국어·영어 이름으로 검색할 수 있어요.",
            vi: "Tìm ngôn ngữ bạn muốn học.<br>Chọn tab hoặc tìm theo tên tiếng Anh hoặc tiếng Hàn.",
            fr: "Trouvez la langue que vous voulez apprendre.<br>Choisissez un onglet ou recherchez par nom en anglais ou en coréen.",
            zh: "找到你想学的语言。<br>可以用上方标签选择，或用英文、韩文名称搜索。"
        },
        "lp.searchPh": {
            en: "Search languages... e.g. Japanese, 일본어", ja: "言語を検索… 例: Japanese, 일본어",
            es: "Buscar idiomas... ej.: Japanese, 일본어", ko: "언어 검색... 예: Japanese, 일본어",
            vi: "Tìm ngôn ngữ... ví dụ: Japanese, 일본어", fr: "Rechercher une langue... ex. : Japanese, 일본어",
            zh: "搜索语言…例如：Japanese、일본어"
        },
        "lp.searchAria": {
            en: "Search languages", ja: "言語を検索", es: "Buscar idiomas", ko: "언어 검색",
            vi: "Tìm ngôn ngữ", fr: "Rechercher une langue", zh: "搜索语言"
        },
        "lp.tabsAria": {
            en: "Select language", ja: "言語を選択", es: "Seleccionar idioma", ko: "언어 선택",
            vi: "Chọn ngôn ngữ", fr: "Choisir une langue", zh: "选择语言"
        },
        "lp.allLanguages": {
            en: "All languages", ja: "すべての言語", es: "Todos los idiomas", ko: "전체 언어",
            vi: "Tất cả ngôn ngữ", fr: "Toutes les langues", zh: "全部语言"
        },
        "lp.allAZ": {
            en: "All languages · A–Z", ja: "すべての言語 · A–Z", es: "Todos los idiomas · A–Z", ko: "전체 언어 · A–Z",
            vi: "Tất cả ngôn ngữ · A–Z", fr: "Toutes les langues · A–Z", zh: "全部语言 · A–Z"
        },
        "lp.languages": {
            en: "Languages", ja: "言語", es: "Idiomas", ko: "언어", vi: "Ngôn ngữ", fr: "Langues", zh: "语言"
        },
        "lp.count1": {
            en: "1 language", ja: "1言語", es: "1 idioma", ko: "1개 언어", vi: "1 ngôn ngữ", fr: "1 langue", zh: "1 种语言"
        },
        "lp.countN": {
            en: "{n} languages", ja: "{n}言語", es: "{n} idiomas", ko: "{n}개 언어", vi: "{n} ngôn ngữ",
            fr: "{n} langues", zh: "{n} 种语言"
        },
        "lp.noResults": {
            en: "No results. Try searching by another language name.",
            ja: "結果がありません。別の言語名で検索してみてください。",
            es: "Sin resultados. Prueba con otro nombre de idioma.",
            ko: "검색 결과가 없습니다. 다른 언어 이름으로 검색해 보세요.",
            vi: "Không có kết quả. Hãy thử tên ngôn ngữ khác.",
            fr: "Aucun résultat. Essayez un autre nom de langue.",
            zh: "没有结果。请尝试其他语言名称。"
        },
        "lp.selectionDefault": {
            en: "Your selected language will appear here.", ja: "選択した言語がここに表示されます。",
            es: "El idioma que elijas aparecerá aquí.", ko: "선택한 언어가 여기에 표시됩니다.",
            vi: "Ngôn ngữ bạn chọn sẽ hiển thị ở đây.", fr: "La langue choisie apparaîtra ici.",
            zh: "你选择的语言会显示在这里。"
        },
        "lp.lastSelected": {
            en: "Last selected: {name} ({native}).", ja: "前回選んだ言語: {name}（{native}）。",
            es: "Último seleccionado: {name} ({native}).", ko: "마지막으로 선택한 언어: {name} ({native}).",
            vi: "Lần chọn gần nhất: {name} ({native}).", fr: "Dernière sélection : {name} ({native}).",
            zh: "上次选择：{name}（{native}）。"
        },
        "lp.nowSelected": {
            en: "Current selection: {name} ({native}). Your choice is saved in this browser.",
            ja: "現在の選択: {name}（{native}）。選択はこのブラウザに保存されます。",
            es: "Selección actual: {name} ({native}). Tu elección se guarda en este navegador.",
            ko: "현재 선택한 언어: {name} ({native}). 언어 선택은 브라우저에 저장됩니다.",
            vi: "Đang chọn: {name} ({native}). Lựa chọn được lưu trong trình duyệt này.",
            fr: "Sélection actuelle : {name} ({native}). Votre choix est enregistré dans ce navigateur.",
            zh: "当前选择：{name}（{native}）。你的选择会保存在此浏览器中。"
        },

        // Vocabulary
        "vocab.eyebrow": {
            en: "Your personal language archive", ja: "あなたの言語アーカイブ", es: "Tu archivo personal de idiomas",
            ko: "나만의 언어 아카이브", vi: "Kho ngôn ngữ cá nhân của bạn", fr: "Votre archive linguistique personnelle",
            zh: "你的个人语言档案"
        },
        "vocab.subtitle": {
            en: "Collect the words you meet. Keep the expressions you love.",
            ja: "出会った単語を集めて、好きな表現を残しましょう。",
            es: "Reúne las palabras que encuentres. Guarda las expresiones que te gusten.",
            ko: "만난 단어를 모으고, 좋아하는 표현을 간직하세요.",
            vi: "Thu thập những từ bạn gặp. Giữ lại những cách diễn đạt bạn yêu thích.",
            fr: "Recueillez les mots rencontrés. Gardez les expressions que vous aimez.",
            zh: "收集遇到的单词，留住喜欢的表达。"
        },
        "vocab.wordsCollected": {
            en: "words collected", ja: "個の単語を収集", es: "palabras guardadas", ko: "개의 단어 수집됨",
            vi: "từ đã lưu", fr: "mots collectés", zh: "个已收集单词"
        },
        "vocab.addTitle": {
            en: "Add a new word", ja: "新しい単語を追加", es: "Añadir una palabra nueva", ko: "새 단어 추가",
            vi: "Thêm từ mới", fr: "Ajouter un nouveau mot", zh: "添加新单词"
        },
        "vocab.addDesc": {
            en: "Save a word, an expression, or something you want to remember.",
            ja: "単語、表現、覚えておきたいことを保存しましょう。",
            es: "Guarda una palabra, una expresión o algo que quieras recordar.",
            ko: "단어, 표현, 기억하고 싶은 것을 저장하세요.",
            vi: "Lưu một từ, một cách diễn đạt hoặc điều bạn muốn nhớ.",
            fr: "Enregistrez un mot, une expression ou quelque chose à retenir.",
            zh: "保存一个单词、一个表达，或任何你想记住的内容。"
        },
        "vocab.fLanguage": {
            en: "Language", ja: "言語", es: "Idioma", ko: "언어", vi: "Ngôn ngữ", fr: "Langue", zh: "语言"
        },
        "vocab.fWord": {
            en: "Word or expression", ja: "単語または表現", es: "Palabra o expresión", ko: "단어 또는 표현",
            vi: "Từ hoặc cách diễn đạt", fr: "Mot ou expression", zh: "单词或表达"
        },
        "vocab.fPron": {
            en: "Pronunciation", ja: "発音", es: "Pronunciación", ko: "발음", vi: "Cách phát âm", fr: "Prononciation", zh: "发音"
        },
        "vocab.fMeaning": {
            en: "Meaning in Korean", ja: "韓国語での意味", es: "Significado en coreano", ko: "뜻",
            vi: "Nghĩa tiếng Hàn", fr: "Sens en coréen", zh: "韩语释义"
        },
        "vocab.fExample": {
            en: "Example sentence", ja: "例文", es: "Frase de ejemplo", ko: "예문", vi: "Câu ví dụ", fr: "Phrase d'exemple", zh: "例句"
        },
        "vocab.fExampleTr": {
            en: "Example translation", ja: "例文の訳", es: "Traducción del ejemplo", ko: "예문 번역",
            vi: "Bản dịch câu ví dụ", fr: "Traduction de l'exemple", zh: "例句翻译"
        },
        "vocab.fNote": {
            en: "Personal note · Context", ja: "個人メモ · 文脈", es: "Nota personal · Contexto", ko: "개인 메모 · 맥락",
            vi: "Ghi chú cá nhân · Ngữ cảnh", fr: "Note personnelle · Contexte", zh: "个人笔记 · 语境"
        },
        "vocab.phExample": {
            en: "Write an example in the language you're learning.",
            ja: "学習中の言語で例文を書いてみましょう。",
            es: "Escribe un ejemplo en el idioma que estudias.",
            ko: "배우는 언어로 예문을 적어 보세요.",
            vi: "Viết một câu ví dụ bằng ngôn ngữ bạn đang học.",
            fr: "Écrivez un exemple dans la langue que vous apprenez.",
            zh: "用你正在学的语言写一个例句。"
        },
        "vocab.phTr": {
            en: "What does this example mean in Korean?", ja: "この例文の韓国語訳を書きましょう。",
            es: "¿Qué significa esta frase en coreano?", ko: "이 예문이 한국어로 어떤 뜻인지 적어 보세요.",
            vi: "Câu này có nghĩa là gì trong tiếng Hàn?", fr: "Que signifie cet exemple en coréen ?",
            zh: "这个例句用韩语是什么意思？"
        },
        "vocab.phNote": {
            en: "Where did you find this? When would you use it? Why do you like it?",
            ja: "どこで見つけましたか？いつ使いますか？なぜ好きですか？",
            es: "¿Dónde lo encontraste? ¿Cuándo lo usarías? ¿Por qué te gusta?",
            ko: "어디서 찾았나요? 언제 쓸 수 있을까요? 왜 좋아하나요?",
            vi: "Bạn tìm thấy ở đâu? Khi nào bạn dùng? Vì sao bạn thích?",
            fr: "Où l'avez-vous trouvé ? Quand l'utiliseriez-vous ? Pourquoi l'aimez-vous ?",
            zh: "你在哪里看到的？什么时候会用？为什么喜欢它？"
        },
        "vocab.searchPh": {
            en: "Search words or meanings...", ja: "単語や意味を検索…", es: "Buscar palabras o significados...",
            ko: "단어나 뜻 검색...", vi: "Tìm từ hoặc nghĩa...", fr: "Rechercher des mots ou des sens...", zh: "搜索单词或释义…"
        },
        "vocab.searchAria": {
            en: "Search vocabulary", ja: "単語を検索", es: "Buscar vocabulario", ko: "단어 검색",
            vi: "Tìm từ vựng", fr: "Rechercher dans le vocabulaire", zh: "搜索词汇"
        },
        "vocab.addBtn": {
            en: "Add to my collection", ja: "単語帳に追加", es: "Añadir a mi colección", ko: "내 단어장에 추가",
            vi: "Thêm vào bộ sưu tập", fr: "Ajouter à ma collection", zh: "加入我的收藏"
        },
        "vocab.collection": {
            en: "Your collection", ja: "あなたのコレクション", es: "Tu colección", ko: "나의 단어 모음",
            vi: "Bộ sưu tập của bạn", fr: "Votre collection", zh: "你的收藏"
        },
        "vocab.markLearned": {
            en: "Mark as learned", ja: "習得済みにする", es: "Marcar como aprendida", ko: "익힘으로 표시",
            vi: "Đánh dấu đã học", fr: "Marquer comme apprise", zh: "标记为已学会"
        },
        "vocab.learned": {
            en: "Learned", ja: "習得済み", es: "Aprendida", ko: "익힘", vi: "Đã học", fr: "Apprise", zh: "已学会"
        },
        "vocab.statusLearned": {
            en: "Learned", ja: "習得済み", es: "Aprendida", ko: "익힘", vi: "Đã học", fr: "Apprise", zh: "已学会"
        },
        "vocab.edit": {
            en: "Edit", ja: "編集", es: "Editar", ko: "수정", vi: "Sửa", fr: "Modifier", zh: "编辑"
        },
        "vocab.sample": {
            en: "Sample entry", ja: "サンプル", es: "Ejemplo", ko: "샘플", vi: "Mẫu", fr: "Exemple", zh: "示例"
        },
        "vocab.justAdded": {
            en: "Just added", ja: "たった今追加", es: "Recién añadida", ko: "방금 추가됨", vi: "Vừa thêm",
            fr: "Ajouté à l'instant", zh: "刚刚添加"
        },
        "vocab.pronNotAdded": {
            en: "Pronunciation not added", ja: "発音未入力", es: "Sin pronunciación", ko: "발음 미입력",
            vi: "Chưa có cách phát âm", fr: "Prononciation non renseignée", zh: "未填写发音"
        },
        "vocab.alertMissing": {
            en: "Please enter the word and its meaning in Korean.", ja: "単語と韓国語での意味を入力してください。",
            es: "Introduce la palabra y su significado en coreano.", ko: "단어와 한국어 뜻을 입력해 주세요.",
            vi: "Vui lòng nhập từ và nghĩa tiếng Hàn.", fr: "Veuillez saisir le mot et son sens en coréen.",
            zh: "请输入单词和韩语释义。"
        },
        "vocab.alertAdded": {
            en: "Your new word has been added!", ja: "新しい単語を追加しました！", es: "¡Tu nueva palabra se ha añadido!",
            ko: "새 단어가 추가되었습니다!", vi: "Đã thêm từ mới của bạn!", fr: "Votre nouveau mot a été ajouté !",
            zh: "新单词已添加！"
        },
        "vocab.footer": {
            en: "Lingua Space · A quiet place for everything you learn.",
            ja: "Lingua Space · 学ぶすべてのための静かな場所。",
            es: "Lingua Space · Un lugar tranquilo para todo lo que aprendes.",
            ko: "Lingua Space · 배우는 모든 것을 위한 조용한 공간.",
            vi: "Lingua Space · Một nơi yên tĩnh cho mọi điều bạn học.",
            fr: "Lingua Space · Un lieu paisible pour tout ce que vous apprenez.",
            zh: "Lingua Space · 为你所学的一切准备的安静空间。"
        },

        // Korean archive
        "kr.brandSuffix": {
            en: "/ KOREAN", ja: "/ 韓国語", es: "/ COREANO", ko: "/ KOREAN", vi: "/ TIẾNG HÀN", fr: "/ CORÉEN", zh: "/ 韩语"
        },
        "kr.eyebrow": {
            en: "A living language archive", ja: "生きた言語アーカイブ", es: "Un archivo vivo del idioma",
            ko: "살아 있는 언어 아카이브", vi: "Kho ngôn ngữ sống động", fr: "Une archive vivante de la langue", zh: "活的语言档案"
        },
        "kr.h1": {
            en: "Things I discovered<br>while using Korean.", ja: "韓国語を使いながら<br>見つけたこと。",
            es: "Lo que descubro<br>usando el coreano.", ko: "한국어를 쓰다가<br>발견한 것들.",
            vi: "Những điều tôi khám phá<br>khi dùng tiếng Hàn.", fr: "Ce que j'ai découvert<br>en utilisant le coréen.",
            zh: "用韩语时<br>发现的那些事。"
        },
        "kr.intro": {
            en: "My own Korean archive for thoughts, unfamiliar expressions, and words I want to remember for a long time.",
            ja: "ふと浮かんだ考え、初めて見た表現、長く覚えておきたい単語を集める、私だけの韓国語アーカイブ。",
            es: "Mi archivo personal de coreano para pensamientos, expresiones desconocidas y palabras que quiero recordar.",
            ko: "떠오른 생각, 낯선 표현, 오래 기억하고 싶은 단어를 모아두는 나만의 한국어 아카이브.",
            vi: "Kho tiếng Hàn riêng của tôi để lưu những suy nghĩ, cách diễn đạt lạ và từ muốn nhớ lâu.",
            fr: "Mon archive personnelle du coréen : idées, expressions inconnues et mots à retenir.",
            zh: "我的韩语档案，收集闪过的想法、陌生的表达和想长久记住的词语。"
        },
        "kr.formTitle": {
            en: "Record a new discovery", ja: "新しい発見を記録", es: "Registrar un nuevo descubrimiento",
            ko: "새로운 발견 기록하기", vi: "Ghi lại một khám phá mới", fr: "Noter une nouvelle découverte", zh: "记录新发现"
        },
        "kr.formHint": {
            en: "Write it down the moment it comes up.", ja: "思いついた瞬間に、気軽に。", es: "Anótalo en cuanto aparezca.",
            ko: "생각나는 순간, 편하게", vi: "Ghi lại ngay khi nghĩ ra.", fr: "Notez-le dès que l'idée vient.", zh: "想到的时候，随手记下"
        },
        "kr.fTitle": {
            en: "What did you discover?", ja: "何を発見しましたか？", es: "¿Qué descubriste?", ko: "무엇을 발견했나요?",
            vi: "Bạn đã khám phá ra điều gì?", fr: "Qu'avez-vous découvert ?", zh: "你发现了什么？"
        },
        "kr.phTitle": {
            en: "e.g. 엉뚱하다 / nuance of a word / a sudden thought",
            ja: "例: 엉뚱하다 / 言葉のニュアンス / ふと浮かんだ考え",
            es: "ej.: 엉뚱하다 / matiz de una palabra / una idea repentina",
            ko: "예: 엉뚱하다 / 말의 뉘앙스 / 갑자기 떠오른 생각",
            vi: "ví dụ: 엉뚱하다 / sắc thái của từ / một suy nghĩ bất chợt",
            fr: "ex. : 엉뚱하다 / nuance d'un mot / une idée soudaine",
            zh: "例如：엉뚱하다 / 词语的语感 / 突然想到的事"
        },
        "kr.fType": {
            en: "Record type", ja: "記録の種類", es: "Tipo de registro", ko: "기록 종류", vi: "Loại ghi chép", fr: "Type de note", zh: "记录类型"
        },
        "kr.t0": { en: "New word", ja: "新しい単語", es: "Palabra nueva", ko: "새로운 단어", vi: "Từ mới", fr: "Nouveau mot", zh: "新单词" },
        "kr.t1": { en: "Fun expression", ja: "面白い表現", es: "Expresión divertida", ko: "재미있는 표현", vi: "Cách diễn đạt thú vị", fr: "Expression amusante", zh: "有趣的表达" },
        "kr.t2": { en: "Unknown word", ja: "知らない言葉", es: "Palabra desconocida", ko: "모르는 말", vi: "Từ chưa biết", fr: "Mot inconnu", zh: "不懂的词" },
        "kr.t3": { en: "Thought", ja: "浮かんだ考え", es: "Pensamiento", ko: "떠오른 생각", vi: "Suy nghĩ", fr: "Idée", zh: "突然的想法" },
        "kr.t4": { en: "Sentence collection", ja: "文の収集", es: "Colección de frases", ko: "문장 수집", vi: "Sưu tập câu", fr: "Collection de phrases", zh: "句子收集" },
        "kr.t5": { en: "Culture & context", ja: "文化と文脈", es: "Cultura y contexto", ko: "문화와 맥락", vi: "Văn hóa & ngữ cảnh", fr: "Culture et contexte", zh: "文化与语境" },
        "kr.t6": { en: "Question", ja: "質問", es: "Pregunta", ko: "질문", vi: "Câu hỏi", fr: "Question", zh: "问题" },
        "kr.allTypes": {
            en: "All record types", ja: "すべての種類", es: "Todos los tipos", ko: "모든 기록 종류", vi: "Tất cả loại", fr: "Tous les types", zh: "全部类型"
        },
        "kr.fSource": {
            en: "Where did you find it?", ja: "どこで見つけましたか？", es: "¿Dónde lo encontraste?", ko: "어디서 발견했나요?",
            vi: "Bạn tìm thấy ở đâu?", fr: "Où l'avez-vous trouvé ?", zh: "在哪里发现的？"
        },
        "kr.phSource": {
            en: "Conversation, video, book, everyday life, etc.", ja: "会話、動画、本、日常など",
            es: "Conversación, vídeo, libro, vida diaria, etc.", ko: "대화, 영상, 책, 일상 등",
            vi: "Hội thoại, video, sách, đời sống...", fr: "Conversation, vidéo, livre, vie quotidienne, etc.",
            zh: "对话、视频、书籍、日常生活等"
        },
        "kr.fNote": {
            en: "What would you like to record?", ja: "記録したい内容", es: "¿Qué quieres registrar?", ko: "기록하고 싶은 내용",
            vi: "Bạn muốn ghi lại điều gì?", fr: "Que voulez-vous noter ?", zh: "想记录的内容"
        },
        "kr.phNote": {
            en: "You don't need to know the meaning. Write where you saw it, why it caught your attention, and what you want to look into later.",
            ja: "意味が分からなくても大丈夫です。どこで見たか、なぜ気になったか、後で調べたいことを自由に書いてください。",
            es: "No hace falta saber el significado. Escribe dónde lo viste, por qué te llamó la atención y qué quieres investigar después.",
            ko: "뜻을 몰라도 괜찮아요. 어떤 상황에서 봤는지, 왜 흥미로웠는지, 나중에 알아보고 싶은 점 등을 자유롭게 적어보세요.",
            vi: "Không cần biết nghĩa cũng được. Hãy viết bạn thấy ở đâu, vì sao thú vị và điều muốn tìm hiểu sau.",
            fr: "Pas besoin de connaître le sens. Notez où vous l'avez vu, pourquoi il vous a intrigué et ce que vous voulez approfondir.",
            zh: "不知道意思也没关系。写下你在哪里看到的、为什么觉得有趣，以及以后想深入了解的地方。"
        },
        "kr.fQuestion": {
            en: "Something to explore later (optional)", ja: "後で調べたいこと（任意）", es: "Algo para explorar más tarde (opcional)",
            ko: "나중에 더 알아보고 싶은 점 (선택)", vi: "Điều muốn tìm hiểu thêm (không bắt buộc)",
            fr: "À explorer plus tard (facultatif)", zh: "以后想进一步了解的问题（可选）"
        },
        "kr.phQuestion": {
            en: "e.g. How is it different from a similar word?", ja: "例: 似た言葉とどう違うのか？",
            es: "ej.: ¿En qué se diferencia de una palabra parecida?", ko: "예: 비슷한 단어와 어떤 차이가 있을까?",
            vi: "ví dụ: Khác gì với từ tương tự?", fr: "ex. : En quoi est-il différent d'un mot proche ?",
            zh: "例如：和相近的词有什么区别？"
        },
        "kr.save": {
            en: "Save to archive", ja: "アーカイブに保存", es: "Guardar en el archivo", ko: "아카이브에 저장하기",
            vi: "Lưu vào kho", fr: "Enregistrer dans l'archive", zh: "保存到档案"
        },
        "kr.notice": {
            en: "Entries are saved to the server. The same archive can be shared across devices.",
            ja: "記録はサーバーに保存されます。同じアーカイブを複数の端末で共有できます。",
            es: "Los registros se guardan en el servidor. El mismo archivo se puede compartir entre dispositivos.",
            ko: "기록은 서버에 저장됩니다. 같은 아카이브를 여러 기기에서 공유할 수 있어요.",
            vi: "Ghi chép được lưu trên máy chủ. Cùng một kho có thể dùng trên nhiều thiết bị.",
            fr: "Les notes sont enregistrées sur le serveur. La même archive peut être partagée entre appareils.",
            zh: "记录保存在服务器上，同一个档案可以在多台设备之间共享。"
        },
        "kr.ideaTitle": {
            en: "Explore Korean more widely", ja: "韓国語をもっと広く探る", es: "Explorar el coreano más a fondo",
            ko: "한국어를 더 넓게 탐색하기", vi: "Khám phá tiếng Hàn rộng hơn", fr: "Explorer le coréen plus largement", zh: "更广泛地探索韩语"
        },
        "kr.ideaDesc": {
            en: "Don't stop at collecting words. Follow the situations they appear in and how people think about them.",
            ja: "単語を集めるだけで終わらせず、その言葉が使われる場面や人々の考え方まで追いかけてみましょう。",
            es: "No te quedes en coleccionar palabras. Sigue las situaciones en las que aparecen y cómo las piensa la gente.",
            ko: "단어를 모으는 데서 끝내지 않고, 그 단어가 쓰이는 상황과 사람들의 생각까지 따라가 볼 수 있어요.",
            vi: "Đừng chỉ dừng ở việc thu thập từ. Hãy xem chúng được dùng trong hoàn cảnh nào và mọi người nghĩ gì về chúng.",
            fr: "Ne vous limitez pas à collectionner des mots. Suivez les situations où ils apparaissent et ce que pensent les gens.",
            zh: "不要只停留在收集单词。也可以看看它们出现的场合，以及人们如何理解它们。"
        },
        "kr.idea1t": {
            en: "01. Tone of words", ja: "01. 言葉のぬくもり", es: "01. Temperatura de las palabras", ko: "01. 말의 온도",
            vi: "01. Sắc thái của từ", fr: "01. La température des mots", zh: "01. 语气的温度"
        },
        "kr.idea1d": {
            en: "Compare expressions with the same meaning that sound warmer or colder.",
            ja: "同じ意味でも、やさしく聞こえたり冷たく聞こえたりする表現を比べる。",
            es: "Compara expresiones con el mismo significado que suenan más cálidas o más frías.",
            ko: "같은 뜻인데 더 다정하거나 차갑게 들리는 표현 비교하기",
            vi: "So sánh các cách nói cùng nghĩa nhưng nghe ấm áp hay lạnh lùng hơn.",
            fr: "Comparez des expressions au même sens qui sonnent plus chaleureuses ou plus froides.",
            zh: "比较意思相近、但听起来更温柔或更冷淡的表达。"
        },
        "kr.idea2t": {
            en: "02. Roots of words", ja: "02. 言葉のルーツ", es: "02. Raíces de las palabras", ko: "02. 단어의 뿌리",
            vi: "02. Gốc của từ", fr: "02. Les racines des mots", zh: "02. 词语的根源"
        },
        "kr.idea2d": {
            en: "Look into etymology, Sino-Korean words, loanwords, and where new slang comes from.",
            ja: "語源、漢語、外来語、新語が生まれた背景を調べる。",
            es: "Investiga etimologías, palabras de origen chino, préstamos y el origen de la jerga nueva.",
            ko: "어원, 한자어, 외래어, 신조어의 탄생 배경 찾아보기",
            vi: "Tìm hiểu từ nguyên, từ Hán Hàn, từ vay mượn và nguồn gốc của từ mới.",
            fr: "Explorez l'étymologie, les mots sino-coréens, les emprunts et l'origine des nouveaux mots.",
            zh: "探索词源、汉字词、外来语，以及新词的诞生背景。"
        },
        "kr.idea3t": {
            en: "03. Korean by situation", ja: "03. 場面別の韓国語", es: "03. Coreano según la situación", ko: "03. 상황별 한국어",
            vi: "03. Tiếng Hàn theo tình huống", fr: "03. Le coréen selon la situation", zh: "03. 按场合看韩语"
        },
        "kr.idea3d": {
            en: "Notice how speech changes with friends, at work, online, and in formal settings.",
            ja: "友達・職場・ネット・公式の場で話し方がどう変わるかを観察する。",
            es: "Observa cómo cambia el tono con amigos, en el trabajo, en línea o en contextos formales.",
            ko: "친구·직장·온라인·공식적인 자리에서 말투가 어떻게 달라지는지 관찰하기",
            vi: "Quan sát cách nói thay đổi khi nói với bạn bè, ở nơi làm việc, trên mạng hay trong trường hợp trang trọng.",
            fr: "Observez comment le ton change entre amis, au travail, en ligne ou en contexte formel.",
            zh: "观察和朋友、同事、网上、正式场合说话时，语气有什么不同。"
        },
        "kr.idea4t": {
            en: "04. My sentence collection", ja: "04. 私だけの文集め", es: "04. Mi colección de frases", ko: "04. 나만의 문장 수집",
            vi: "04. Bộ sưu tập câu của tôi", fr: "04. Ma collection de phrases", zh: "04. 我的句子收藏"
        },
        "kr.idea4d": {
            en: "Record sentences you want to remember and why you liked them.",
            ja: "長く覚えておきたい文と、その文が好きな理由を記録する。",
            es: "Anota las frases que quieres recordar y por qué te gustan.",
            ko: "오래 기억하고 싶은 문장과 그 문장이 좋았던 이유 기록하기",
            vi: "Ghi lại những câu bạn muốn nhớ lâu và lý do bạn thích chúng.",
            fr: "Notez les phrases que vous voulez retenir et pourquoi vous les aimez.",
            zh: "记录你想长久记住的句子，以及喜欢它的理由。"
        },
        "kr.idea5t": {
            en: "05. Expression lab", ja: "05. 表現の実験室", es: "05. Laboratorio de expresiones", ko: "05. 표현 실험실",
            vi: "05. Phòng thí nghiệm diễn đạt", fr: "05. Laboratoire d'expressions", zh: "05. 表达实验室"
        },
        "kr.idea5d": {
            en: "Rewrite one sentence in several tones and compare how each feels.",
            ja: "ひとつの文をいろいろな語調に変えて、感じの違いを比べる。",
            es: "Reescribe una frase con distintos tonos y compara cómo cambia su sentido.",
            ko: "하나의 문장을 여러 말투로 바꿔보고 느낌 비교하기",
            vi: "Viết lại một câu theo nhiều giọng điệu khác nhau và so sánh cảm giác.",
            fr: "Réécrivez une phrase sur plusieurs tons et comparez leur effet.",
            zh: "把同一句话换成几种语气，比较各自的感觉。"
        },
        "kr.missionTitle": {
            en: "A small exploration mission", ja: "小さな探索ミッション", es: "Una pequeña misión de exploración",
            ko: "작은 탐색 미션", vi: "Nhiệm vụ khám phá nhỏ", fr: "Une petite mission d'exploration", zh: "小小探索任务"
        },
        "kr.missionDesc": {
            en: "You don't have to study like it's homework. Just notice one word you came across today.",
            ja: "勉強のように構えなくて大丈夫です。今日目に留まった言葉をひとつ観察してみてください。",
            es: "No hace falta estudiar como si fuera una tarea. Solo fíjate en una palabra que hayas visto hoy.",
            ko: "공부처럼 하지 않아도 돼요. 오늘 눈에 들어온 말 하나를 관찰해 보세요.",
            vi: "Không cần học như làm bài tập. Chỉ cần để ý một từ bạn gặp hôm nay.",
            fr: "Pas besoin d'étudier comme un devoir. Observez simplement un mot croisé aujourd'hui.",
            zh: "不必当作功课来学。只要留意一个今天遇到的词就好。"
        },
        "kr.chip1": { en: "Today's new slang", ja: "今日の新語", es: "Jerga del día", ko: "오늘의 신조어", vi: "Từ lóng hôm nay", fr: "Argot du jour", zh: "今日新词" },
        "kr.chip2": { en: "Compare similar words", ja: "似た言葉を比べる", es: "Comparar palabras parecidas", ko: "비슷한 말 비교", vi: "So sánh từ tương tự", fr: "Comparer des mots proches", zh: "比较近义词" },
        "kr.chip3": { en: "Change the tone", ja: "語調を変える", es: "Cambiar el tono", ko: "말투 바꾸기", vi: "Đổi giọng điệu", fr: "Changer de ton", zh: "改变语气" },
        "kr.chip4": { en: "Collect sentences", ja: "文を集める", es: "Coleccionar frases", ko: "문장 수집", vi: "Sưu tập câu", fr: "Collecter des phrases", zh: "收集句子" },
        "kr.chip5": { en: "Regional expressions", ja: "地域の表現", es: "Expresiones regionales", ko: "지역별 표현", vi: "Cách nói theo vùng", fr: "Expressions régionales", zh: "地方表达" },
        "kr.chip6": { en: "Generational language", ja: "世代の言葉", es: "Lenguaje generacional", ko: "세대별 언어", vi: "Ngôn ngữ theo thế hệ", fr: "Langage des générations", zh: "代际语言" },
        "kr.chip7": { en: "Internet memes", ja: "ネットミーム", es: "Memes de internet", ko: "인터넷 밈", vi: "Meme trên mạng", fr: "Mèmes internet", zh: "网络梗" },
        "kr.chip8": { en: "Words hard to translate", ja: "翻訳しにくい言葉", es: "Palabras difíciles de traducir", ko: "번역하기 어려운 말", vi: "Từ khó dịch", fr: "Mots difficiles à traduire", zh: "难以翻译的词" },
        "kr.archiveTitle": {
            en: "My discovery archive", ja: "私の発見アーカイブ", es: "Mi archivo de descubrimientos", ko: "나의 발견 아카이브",
            vi: "Kho khám phá của tôi", fr: "Mon archive de découvertes", zh: "我的发现档案"
        },
        "kr.loading": {
            en: "Loading your archive…", ja: "アーカイブを読み込み中…", es: "Cargando tu archivo…", ko: "기록을 불러오는 중…",
            vi: "Đang tải kho của bạn…", fr: "Chargement de votre archive…", zh: "正在加载你的档案…"
        },
        "kr.emptyCount": {
            en: "No records saved yet.", ja: "まだ保存された記録はありません。", es: "Todavía no hay registros guardados.",
            ko: "아직 저장된 기록이 없어요.", vi: "Chưa có ghi chép nào được lưu.", fr: "Aucune note enregistrée pour l'instant.",
            zh: "还没有保存的记录。"
        },
        "kr.countLine": {
            en: "{total} records · {shown} shown", ja: "全{total}件 · 表示中 {shown}件", es: "{total} registros · {shown} mostrados",
            ko: "총 {total}개의 기록 · 화면에 {shown}개 표시", vi: "Tổng {total} ghi chép · đang hiện {shown}",
            fr: "{total} notes · {shown} affichées", zh: "共 {total} 条记录 · 显示 {shown} 条"
        },
        "kr.export": {
            en: "Export records", ja: "記録を書き出す", es: "Exportar registros", ko: "기록 내보내기",
            vi: "Xuất ghi chép", fr: "Exporter les notes", zh: "导出记录"
        },
        "kr.searchPh": {
            en: "Search words, notes, or questions", ja: "単語・内容・質問を検索", es: "Buscar palabras, notas o preguntas",
            ko: "단어, 내용, 질문 검색", vi: "Tìm từ, nội dung hoặc câu hỏi", fr: "Rechercher des mots, notes ou questions", zh: "搜索词语、内容或问题"
        },
        "kr.empty": {
            en: "No records yet.<br>Save your first discovery.", ja: "まだ記録がありません。<br>最初の発見を残してみましょう。",
            es: "Aún no hay registros.<br>Guarda tu primer descubrimiento.", ko: "아직 기록이 없어요.<br>첫 번째 발견을 남겨보세요.",
            vi: "Chưa có ghi chép nào.<br>Hãy lưu khám phá đầu tiên của bạn.", fr: "Aucune note pour l'instant.<br>Enregistrez votre première découverte.",
            zh: "还没有记录。<br>保存你的第一个发现吧。"
        },
        "kr.noSearchResults": {
            en: "No matching records.", ja: "一致する記録がありません。", es: "No hay registros que coincidan.",
            ko: "검색 결과가 없어요.", vi: "Không có ghi chép phù hợp.", fr: "Aucune note correspondante.", zh: "没有匹配的记录。"
        },
        "kr.questionLabel": {
            en: "Want to explore:", ja: "さらに知りたいこと:", es: "Quiero explorar:", ko: "더 알아볼 점:",
            vi: "Muốn tìm hiểu thêm:", fr: "À explorer :", zh: "想进一步了解："
        },
        "kr.deleteAria": {
            en: "Delete record", ja: "記録を削除", es: "Eliminar registro", ko: "기록 삭제", vi: "Xóa ghi chép", fr: "Supprimer la note", zh: "删除记录"
        },
        "kr.confirmDelete": {
            en: "Delete this record?", ja: "この記録を削除しますか？", es: "¿Eliminar este registro?", ko: "이 기록을 삭제할까요?",
            vi: "Xóa ghi chép này?", fr: "Supprimer cette note ?", zh: "删除这条记录？"
        },
        "kr.deleteFailed": {
            en: "Delete failed.", ja: "削除に失敗しました。", es: "No se pudo eliminar.", ko: "삭제에 실패했어요.",
            vi: "Xóa thất bại.", fr: "Échec de la suppression.", zh: "删除失败。"
        },
        "kr.saveFailed": {
            en: "Save failed. Please check your internet connection.", ja: "保存に失敗しました。インターネット接続を確認してください。",
            es: "No se pudo guardar. Revisa tu conexión a internet.", ko: "저장에 실패했어요. 인터넷 연결을 확인해 주세요.",
            vi: "Lưu thất bại. Vui lòng kiểm tra kết nối mạng.", fr: "Échec de l'enregistrement. Vérifiez votre connexion.",
            zh: "保存失败，请检查网络连接。"
        },
        "kr.loadFailed": {
            en: "Couldn't load records. Please refresh.", ja: "記録を読み込めませんでした。再読み込みしてください。",
            es: "No se pudieron cargar los registros. Actualiza la página.", ko: "기록을 불러오지 못했어요. 새로고침해 주세요.",
            vi: "Không tải được ghi chép. Vui lòng tải lại trang.", fr: "Impossible de charger les notes. Actualisez la page.",
            zh: "无法加载记录，请刷新页面。"
        },
        "kr.oldReadFail": {
            en: "Couldn't read the old entries. Check the backup with Export first.",
            ja: "以前の記録を読み込めませんでした。先に「書き出す」でバックアップを確認してください。",
            es: "No se pudieron leer los registros antiguos. Revisa la copia con Exportar primero.",
            ko: "기존 기록을 읽을 수 없어요. 기록 내보내기로 백업을 확인해 주세요.",
            vi: "Không đọc được ghi chép cũ. Vui lòng kiểm tra bản sao lưu bằng chức năng Xuất trước.",
            fr: "Impossible de lire les anciennes notes. Vérifiez d'abord la sauvegarde via Exporter.",
            zh: "无法读取旧记录，请先用“导出记录”检查备份。"
        },
        "kr.migrated": {
            en: "Moved {n} saved entries from this browser to the server.",
            ja: "この端末の記録を{n}件サーバーに移しました。",
            es: "Se pasaron {n} registros de este navegador al servidor.",
            ko: "{n}개의 기존 기록을 서버로 옮겼어요.",
            vi: "Đã chuyển {n} ghi chép từ trình duyệt này lên máy chủ.",
            fr: "{n} notes de ce navigateur ont été transférées sur le serveur.",
            zh: "已将此浏览器中的 {n} 条记录迁移到服务器。"
        },

        // Study requests
        "study.h1": {
            en: "What do you want to learn?", ja: "何を学びたいですか？", es: "¿Qué quieres aprender?",
            ko: "무엇을 배우고 싶나요?", vi: "Bạn muốn học gì?", fr: "Que voulez-vous apprendre ?", zh: "你想学什么？"
        },
        "study.selectLang": {
            en: "Select a language", ja: "言語を選択", es: "Selecciona un idioma", ko: "언어를 선택하세요",
            vi: "Chọn ngôn ngữ", fr: "Choisir une langue", zh: "选择语言"
        },
        "study.goal": {
            en: "My goal", ja: "目標", es: "Mi objetivo", ko: "나의 목표", vi: "Mục tiêu của tôi", fr: "Mon objectif", zh: "我的目标"
        },
        "study.phGoal": {
            en: "e.g. Travel conversation", ja: "例: 旅行で会話する", es: "ej.: Conversación para viajar", ko: "예: 여행 회화",
            vi: "ví dụ: Hội thoại du lịch", fr: "ex. : Conversation pour voyager", zh: "例如：旅行会话"
        },
        "study.topic": {
            en: "What do you want to learn?", ja: "学びたいこと", es: "Qué quieres aprender", ko: "배우고 싶은 것",
            vi: "Điều bạn muốn học", fr: "Ce que vous voulez apprendre", zh: "想学的内容"
        },
        "study.phTopic": {
            en: "e.g. Ordering food at a restaurant", ja: "例: レストランで注文する", es: "ej.: Pedir comida en un restaurante",
            ko: "예: 식당에서 주문하기", vi: "ví dụ: Gọi món ở nhà hàng", fr: "ex. : Commander au restaurant", zh: "例如：在餐厅点餐"
        },
        "study.difficult": {
            en: "What is difficult for you?", ja: "難しいと感じること", es: "¿Qué se te hace difícil?", ko: "어려운 점",
            vi: "Điều gì khó với bạn?", fr: "Qu'est-ce qui vous est difficile ?", zh: "你觉得哪里难？"
        },
        "study.phDifficult": {
            en: "e.g. I don't know how to speak politely", ja: "例: 丁寧な話し方がわからない", es: "ej.: No sé cómo hablar con cortesía",
            ko: "예: 공손하게 말하는 법을 모르겠어요", vi: "ví dụ: Tôi không biết nói lịch sự", fr: "ex. : Je ne sais pas parler poliment",
            zh: "例如：不知道怎样礼貌地说话"
        },
        "study.save": {
            en: "Save my study request", ja: "学習リクエストを保存", es: "Guardar mi solicitud de estudio", ko: "학습 요청 저장하기",
            vi: "Lưu yêu cầu học của tôi", fr: "Enregistrer ma demande d'étude", zh: "保存我的学习需求"
        },
        "study.savedTitle": {
            en: "Saved study requests", ja: "保存された学習リクエスト", es: "Solicitudes de estudio guardadas", ko: "저장된 학습 요청",
            vi: "Yêu cầu học đã lưu", fr: "Demandes d'étude enregistrées", zh: "已保存的学习需求"
        },
        "study.none": {
            en: "No study requests yet.", ja: "まだ学習リクエストはありません。", es: "Aún no hay solicitudes de estudio.",
            ko: "아직 학습 요청이 없어요.", vi: "Chưa có yêu cầu học nào.", fr: "Aucune demande d'étude pour l'instant.", zh: "还没有学习需求。"
        },
        "study.goalLabel": {
            en: "Goal:", ja: "目標:", es: "Objetivo:", ko: "목표:", vi: "Mục tiêu:", fr: "Objectif :", zh: "目标："
        }
    };

    function getLang() {
        try {
            const saved = localStorage.getItem(STORAGE_KEY);
            if (LANGS.some(l => l.code === saved)) return saved;
        } catch (e) { /* storage blocked: fall back to English */ }
        return "en";
    }

    let current = getLang();
    const listeners = [];

    function t(key, vars) {
        const entry = T[key];
        let text = entry ? (entry[current] || entry.en) : key;
        if (vars) {
            Object.keys(vars).forEach(name => {
                text = text.split("{" + name + "}").join(String(vars[name]));
            });
        }
        return text;
    }

    function locale() {
        const found = LANGS.find(l => l.code === current);
        return found ? found.locale : "en-US";
    }

    function applyStatic() {
        document.documentElement.lang = current;

        document.querySelectorAll("[data-i18n]").forEach(el => {
            el.textContent = t(el.dataset.i18n);
        });
        document.querySelectorAll("[data-i18n-html]").forEach(el => {
            el.innerHTML = t(el.dataset.i18nHtml);
        });
        document.querySelectorAll("[data-i18n-ph]").forEach(el => {
            el.setAttribute("placeholder", t(el.dataset.i18nPh));
        });
        document.querySelectorAll("[data-i18n-aria]").forEach(el => {
            el.setAttribute("aria-label", t(el.dataset.i18nAria));
        });
    }

    function fillPickers() {
        document.querySelectorAll("select[data-lang-picker]").forEach(select => {
            select.replaceChildren();
            LANGS.forEach(l => {
                const option = document.createElement("option");
                option.value = l.code;
                option.textContent = l.label;
                option.selected = l.code === current;
                select.appendChild(option);
            });
        });
    }

    function setLang(code) {
        if (!LANGS.some(l => l.code === code)) return;
        current = code;
        try { localStorage.setItem(STORAGE_KEY, code); } catch (e) { /* ignore */ }
        applyStatic();
        fillPickers();
        listeners.forEach(fn => fn(current));
    }

    function onChange(fn) {
        listeners.push(fn);
    }

    function injectStyle() {
        const style = document.createElement("style");
        style.textContent =
            ".ls-picker-slot{margin:0 0 16px}" +
            ".ls-lang-picker{font:inherit;font-size:13px;padding:6px 12px;border:1px solid #cfd3c8;" +
            "border-radius:999px;background:#fff;color:inherit;max-width:100%;cursor:pointer}";
        document.head.appendChild(style);
    }

    function init() {
        injectStyle();
        document.querySelectorAll("select[data-lang-picker]").forEach(select => {
            select.addEventListener("change", () => setLang(select.value));
        });
        applyStatic();
        fillPickers();
        listeners.forEach(fn => fn(current));
    }

    window.LS = { t, setLang, getLang: () => current, onChange, locale, LANGS };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
