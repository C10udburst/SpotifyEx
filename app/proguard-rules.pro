# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Repackage classes into the top-level.
-repackageclasses

# Amount of optimization iterations, taken from an SO post
-optimizationpasses 5

# Broaden access modifiers to increase results during optimization
-allowaccessmodification

-keep class io.github.cloudburst.spotifyex.Module { *; }
