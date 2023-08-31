
#ifndef JXL_THREADS_EXPORT_H
#define JXL_THREADS_EXPORT_H

#ifdef JXL_THREADS_STATIC_DEFINE
#  define JXL_THREADS_EXPORT
#  define JXL_THREADS_NO_EXPORT
#else
#  ifndef JXL_THREADS_EXPORT
#    ifdef JXL_THREADS_INTERNAL_LIBRARY_BUILD
        /* We are building this library */
#      define JXL_THREADS_EXPORT __attribute__((visibility("default")))
#    else
        /* We are using this library */
#      define JXL_THREADS_EXPORT __attribute__((visibility("default")))
#    endif
#  endif

#  ifndef JXL_THREADS_NO_EXPORT
#    define JXL_THREADS_NO_EXPORT __attribute__((visibility("hidden")))
#  endif
#endif

#ifndef JXL_THREADS_DEPRECATED
#  define JXL_THREADS_DEPRECATED __attribute__ ((__deprecated__))
#endif

#ifndef JXL_THREADS_DEPRECATED_EXPORT
#  define JXL_THREADS_DEPRECATED_EXPORT JXL_THREADS_EXPORT JXL_THREADS_DEPRECATED
#endif

#ifndef JXL_THREADS_DEPRECATED_NO_EXPORT
#  define JXL_THREADS_DEPRECATED_NO_EXPORT JXL_THREADS_NO_EXPORT JXL_THREADS_DEPRECATED
#endif

#if 0 /* DEFINE_NO_DEPRECATED */
#  ifndef JXL_THREADS_NO_DEPRECATED
#    define JXL_THREADS_NO_DEPRECATED
#  endif
#endif

#endif /* JXL_THREADS_EXPORT_H */
