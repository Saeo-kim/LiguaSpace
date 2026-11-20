// Lingua Space — request feedback
// Shows a small banner when a server request is taking a while (for example, when the
// free hosting service is waking up). Usage: LSNet.track(fetch(...)).
(function () {
    const SLOW_AFTER_MS = 2500;
    let pending = 0;
    let timer = null;
    let banner = null;

    function ensureBanner() {
        if (banner) return banner;
        const style = document.createElement("style");
        style.textContent =
            ".ls-net-banner{position:fixed;left:50%;transform:translateX(-50%);top:12px;z-index:100;" +
            "display:flex;align-items:center;gap:10px;max-width:calc(100% - 32px);padding:10px 14px;" +
            "border-radius:12px;background:#3C4539;color:#fff;font:14px/1.4 Arial,'Malgun Gothic',sans-serif;" +
            "box-shadow:0 6px 20px rgba(0,0,0,.18)}" +
            ".ls-net-banner[hidden]{display:none}" +
            ".ls-net-spin{width:16px;height:16px;border:2px solid rgba(255,255,255,.35);border-top-color:#fff;" +
            "border-radius:50%;animation:ls-net-spin .8s linear infinite;flex-shrink:0}" +
            "@keyframes ls-net-spin{to{transform:rotate(360deg)}}" +
            "@media (prefers-reduced-motion:reduce){.ls-net-spin{animation-duration:2.4s}}";
        document.head.appendChild(style);

        banner = document.createElement("div");
        banner.className = "ls-net-banner";
        banner.setAttribute("role", "status");
        banner.setAttribute("aria-live", "polite");
        banner.hidden = true;

        const spin = document.createElement("span");
        spin.className = "ls-net-spin";
        spin.setAttribute("aria-hidden", "true");

        const text = document.createElement("span");
        text.textContent = "Still connecting… the server may be waking up. The first load can take up to a minute.";

        banner.append(spin, text);
        document.body.appendChild(banner);
        return banner;
    }

    function show() {
        ensureBanner().hidden = false;
    }

    function hide() {
        if (banner) banner.hidden = true;
    }

    function track(promise) {
        pending += 1;
        if (!timer) timer = setTimeout(show, SLOW_AFTER_MS);

        return promise.finally(() => {
            pending -= 1;
            if (pending === 0) {
                clearTimeout(timer);
                timer = null;
                hide();
            }
        });
    }

    window.LSNet = { track };
})();
