/* ============================================================
   SNIKERS — Slider Hero + interacciones
   ============================================================ */

(function() {
    'use strict';

    // ========= HERO SLIDER =========
    class HeroSlider {
        constructor(root) {
            this.root = root;
            this.slides = root.querySelectorAll('.snk-slide');
            this.dots = root.querySelectorAll('.snk-slide-dot');
            this.prevBtn = root.querySelector('.snk-slide-arrow.prev');
            this.nextBtn = root.querySelector('.snk-slide-arrow.next');
            this.currentEl = root.querySelector('.snk-slide-counter .current');
            this.totalEl = root.querySelector('.snk-slide-counter .total');
            this.current = 0;
            this.interval = null;
            this.duration = 6000; // 6s por slide
            this.isTransitioning = false;

            if (this.slides.length === 0) return;
            this.init();
        }

        init() {
            if (this.totalEl) this.totalEl.textContent = String(this.slides.length).padStart(2, '0');
            this.goTo(0);
            this.bindEvents();
            this.startAuto();
        }

        bindEvents() {
            if (this.prevBtn) this.prevBtn.addEventListener('click', () => this.prev());
            if (this.nextBtn) this.nextBtn.addEventListener('click', () => this.next());
            this.dots.forEach((dot, i) => dot.addEventListener('click', () => this.goTo(i)));

            // Pausar al hacer hover
            this.root.addEventListener('mouseenter', () => this.stopAuto());
            this.root.addEventListener('mouseleave', () => this.startAuto());

            // Teclado
            document.addEventListener('keydown', (e) => {
                if (e.key === 'ArrowLeft') this.prev();
                if (e.key === 'ArrowRight') this.next();
            });

            // Swipe táctil
            let touchStartX = 0;
            this.root.addEventListener('touchstart', (e) => {
                touchStartX = e.changedTouches[0].screenX;
            }, { passive: true });

            this.root.addEventListener('touchend', (e) => {
                const touchEndX = e.changedTouches[0].screenX;
                const diff = touchStartX - touchEndX;
                if (Math.abs(diff) > 50) {
                    if (diff > 0) this.next(); else this.prev();
                }
            }, { passive: true });

            // Pausar si la pestaña pierde foco
            document.addEventListener('visibilitychange', () => {
                if (document.hidden) this.stopAuto(); else this.startAuto();
            });
        }

        goTo(index) {
            if (this.isTransitioning || index === this.current && this.slides[index].classList.contains('active')) {
                // Permite activar el primero inicial
                if (!this.slides[0].classList.contains('active') && index === 0) {
                    // continúa
                } else if (this.isTransitioning) {
                    return;
                }
            }
            this.isTransitioning = true;

            this.slides.forEach(s => s.classList.remove('active'));
            this.dots.forEach(d => d.classList.remove('active'));

            this.slides[index].classList.add('active');
            if (this.dots[index]) this.dots[index].classList.add('active');

            this.current = index;
            if (this.currentEl) this.currentEl.textContent = String(index + 1).padStart(2, '0');

            // Reinicia el timer de progreso visual
            this.dots.forEach(d => {
                const clone = d.cloneNode(true);
                d.parentNode.replaceChild(clone, d);
            });
            this.dots = this.root.querySelectorAll('.snk-slide-dot');
            this.dots.forEach((dot, i) => {
                dot.addEventListener('click', () => this.goTo(i));
                if (i === this.current) dot.classList.add('active');
            });

            setTimeout(() => { this.isTransitioning = false; }, 1200);
            this.restartAuto();
        }

        next() { this.goTo((this.current + 1) % this.slides.length); }
        prev() { this.goTo((this.current - 1 + this.slides.length) % this.slides.length); }

        startAuto() {
            this.stopAuto();
            this.interval = setInterval(() => this.next(), this.duration);
        }

        stopAuto() {
            if (this.interval) {
                clearInterval(this.interval);
                this.interval = null;
            }
        }

        restartAuto() {
            if (this.interval !== null) this.startAuto();
        }
    }

    // ========= SIZE SELECTOR =========
    function initSizeSelector() {
        document.querySelectorAll('.snk-size-grid').forEach(grid => {
            const options = grid.querySelectorAll('.snk-size-option');
            options.forEach(opt => {
                opt.addEventListener('click', () => {
                    options.forEach(o => o.classList.remove('selected'));
                    opt.classList.add('selected');
                    const input = opt.querySelector('input[type="radio"]');
                    if (input) input.checked = true;
                });
            });
        });
    }

    // ========= SCROLL REVEAL =========
    function initScrollReveal() {
        if (!('IntersectionObserver' in window)) return;
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1, rootMargin: '0px 0px -10% 0px' });

        document.querySelectorAll('.snk-product-card, .snk-category-card, .snk-section-header')
            .forEach(el => observer.observe(el));
    }

    // ========= NAVBAR SCROLL EFFECT =========
    function initNavbarScroll() {
        const nav = document.querySelector('.snk-navbar');
        if (!nav) return;
        let lastScroll = 0;
        window.addEventListener('scroll', () => {
            const scroll = window.scrollY;
            if (scroll > 80) nav.classList.add('scrolled');
            else nav.classList.remove('scrolled');
            lastScroll = scroll;
        });
    }

    // ========= INIT =========
    document.addEventListener('DOMContentLoaded', () => {
        const heroSlider = document.querySelector('.snk-hero-slider');
        if (heroSlider) new HeroSlider(heroSlider);
        initSizeSelector();
        initScrollReveal();
        initNavbarScroll();
    });

})();
